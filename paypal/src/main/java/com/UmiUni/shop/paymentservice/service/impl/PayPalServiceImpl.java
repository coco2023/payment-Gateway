package com.UmiUni.shop.paymentservice.service.impl;

import com.UmiUni.shop.paymentservice.constant.ErrorCategory;
import com.UmiUni.shop.paymentservice.constant.OrderStatus;
import com.UmiUni.shop.paymentservice.constant.PaymentStatus;
import com.UmiUni.shop.paymentservice.entity.PayPalPayment;
import com.UmiUni.shop.paymentservice.entity.PayPalPaymentResponseEntity;
import com.UmiUni.shop.paymentservice.exception.PaymentExpiredException;
import com.UmiUni.shop.paymentservice.exception.PaymentProcessingException;
import com.UmiUni.shop.paymentservice.external.product.ProductService;
import com.UmiUni.shop.paymentservice.external.salesOrder.SalesOrderDetailService;
import com.UmiUni.shop.paymentservice.external.salesOrder.SalesOrderService;
import com.UmiUni.shop.paymentservice.external.supplier.SupplierService;
import com.UmiUni.shop.paymentservice.model.*;
import com.UmiUni.shop.paymentservice.repository.PayPalPaymentRepository;
import com.UmiUni.shop.paymentservice.repository.PayPalPaymentResponseRepo;
import com.UmiUni.shop.paymentservice.service.PayPalService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
public class PayPalServiceImpl implements PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Value("${paypal.frontend.base.uri}") // paypal.frontend.base.test  // paypal.frontend.base.uri
    private String frontendUrl;           // "http://localhost:3000"   // https://www.quickmall24.com

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private PayPalPaymentRepository payPalPaymentRepository;

    @Autowired
    private SalesOrderDetailService salesOrderDetailService;

    @Autowired
    private PayPalPaymentResponseRepo payPalPaymentResponseRepo;

    private APIContext createApiContextForSupplier(Long supplierId) {
        Supplier supplier = supplierService.getSupplierById(supplierId).getBody(); // supplierRepository.findById(supplierId)
        String supplierClientId = supplier.getPaypalClientId();
        String supplierSecret = supplier.getPaypalClientSecret();

        APIContext apiContext = new APIContext(supplierClientId, supplierSecret, "sandbox");
        return apiContext;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public PaymentResponse createPayment(SalesOrder salesOrderRequest) {

        log.info("start create payment");
        SalesOrder salesOrder = salesOrderService.getSalesOrderBySalesOrderSn(salesOrderRequest.getSalesOrderSn()).getBody();

        // TODO: get clientId and secret of the current supplier and create a new apiContext
//        APIContext apiContext = getAPIContext();
        APIContext apiContext = createApiContextForSupplier(salesOrder.getSupplierId());

        // get salesOrderSn
        String salesOrderSn = salesOrder.getSalesOrderSn();
        // get salesOrder expiredTime
        LocalDateTime expiredTime = salesOrder.getExpirationDate();
        log.info(expiredTime);
        // get supplierId
        Long supplierId = salesOrder.getSupplierId();

        // Logic to create a payment
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(salesOrder.getTotalAmount().toString());

        Transaction transaction = new Transaction();
        // Save SalesOrderSn
        transaction.setCustom(salesOrderSn);
        transaction.setAmount(amount);
        // convert expiredTime into string and save
        transaction.setDescription(expiredTime.toString());  // save expiredTime as description

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);


        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(frontendUrl + "/paypal-return?salesOrderSn=" + salesOrderSn + "&supplierId=" + supplierId);
        redirectUrls.setReturnUrl(frontendUrl + "/paypal-success?salesOrderSn=" + salesOrderSn + "&supplierId=" + supplierId);
        payment.setRedirectUrls(redirectUrls);
        log.info("redirectUrls: " + redirectUrls);

        Payment createdPayment = null;
        PayPalPayment payPalPayment = null;

        try {

            if ( salesOrder.getTotalAmount().compareTo(BigDecimal.ZERO) < 0 ) {
                // TODO: save the error logs during PayPal payment action
                throw new PaymentProcessingException("Payment processing failed because the payment amount is negative.");
            }

            if (customerHasInsufficientFunds(salesOrder.getCustomerId(), salesOrder.getTotalAmount())) {
                throw new PaymentProcessingException("Payment processing failed due to insufficient funds.");
            }

            if (customerExitsDuringPayment()) {
                throw new PaymentProcessingException("Payment creation interrupted because the customer exited.");
            }

            createdPayment = payment.create(apiContext);
            LocalDateTime now = LocalDateTime.now();

            String payPalTransactionId = extractPaymentId(createdPayment);

            String payPalPaymentState = extractPaymentStatus(createdPayment);

            // Find the approval URL
            String approvalUrl = extractApprovalUrl(createdPayment);

            // Find the token
            String token = extractToken(approvalUrl);

            // Save the PayPalPaymentEntity
            payPalPayment = PayPalPayment.builder()
                    .paypalToken(token)
                    .salesOrderSn(salesOrderSn)
                    .transactionId(payPalTransactionId)
                    .paymentState(payPalPaymentState)
                    .createTime(now)
                    .updatedAt(now)
                    .paymentMethod("PayPal")
                    .supplierId(String.valueOf(supplierId))
                    .approvalUrl(approvalUrl)
                    .build();
            payPalPaymentRepository.save(payPalPayment);
            log.info("createPayment: " + createdPayment);
            log.info("payPalPayment: " + payPalPayment);

            // update the orderStatus
            salesOrder.setOrderStatus(OrderStatus.PENDING);
            salesOrder.setLastUpdated(
                    new Date()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );

            // Lock the Product Inventory
            // get order by salesOrderSn
            List<SalesOrderDetail> salesOrderDetailList = salesOrderDetailService.getSalesOrderDetailsBySalesOrderSn(salesOrderSn).getBody(); // salesOrderDetailRepository.findSalesOrderDetailsBySalesOrderSn(salesOrderSn);
            for (SalesOrderDetail salesOrderDetail : salesOrderDetailList) {
                String skuCode = salesOrderDetail.getSkuCode();
                int quantity = salesOrderDetail.getQuantity();
                log.info("create: salesOrderDetails product Info: " + skuCode + " " + quantity);

                // Lock inventory - part of the database transaction
                productService.lockInventory(skuCode, quantity);

//                // After successful transaction, send message to RabbitMQ
//                rabbitMQSender.sendInventoryLock(new InventoryUpdateMessage(skuCode, quantity));
            }

            return new PaymentResponse("success create payment!", createdPayment.getId(), null, null, approvalUrl);
        } catch (PaymentProcessingException e) {
//            return  paymentErrorHandlingService.handlePaymentProcessingError(e, payPalPayment.getTransactionId(), salesOrder.getSalesOrderSn());
        } catch (PayPalRESTException e) {
//            return paymentErrorHandlingService.handlePayPalRESTError(e, payPalPayment.getTransactionId(), salesOrder.getSalesOrderSn());
        } catch (Exception ex) {
//            return paymentErrorHandlingService.handleGenericError(ex, payPalPayment.getTransactionId(), salesOrder.getSalesOrderSn());
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public PaymentResponse completePayment(String paymentId, String payerId, String supplierId) {

//        APIContext apiContext = getAPIContext();
        APIContext apiContext = createApiContextForSupplier(Long.valueOf(supplierId));

        PayPalPayment payPalPayment = null;

        try {

            if (customerExitsDuringPayment()) {
                throw new PaymentProcessingException("Payment creation interrupted because the customer exited.");
            }

            // check if payment has been expired
            // Retrieve the payment object from PayPal
            Payment getCreatePayment = Payment.get(apiContext, paymentId);

            // get cart(Token)
            payPalPayment = payPalPaymentRepository.findByPaypalToken("EC-" + getCreatePayment.getCart());
            log.info("**payPalPayment: " + payPalPayment);

            // get expireDate
            String expiredDate = extractDescription(getCreatePayment);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime expiredTime = LocalDateTime.parse(expiredDate, formatter);
            log.info("this is expiredDate: " + expiredDate + " now: " + LocalDateTime.now());

            LocalDateTime now = LocalDateTime.now();

            if (expiredTime.isBefore(now)) {
                // update SalesOrder Status to EXPIRED
                // get salesOrderSn
                String salesOrderSn = extractCustomOrderSn(getCreatePayment);
                SalesOrder salesOrder = salesOrderService.getSalesOrderBySalesOrderSn(salesOrderSn).getBody();
                salesOrder.setOrderStatus(OrderStatus.EXPIRED);
                salesOrder.setLastUpdated(now);
                log.info("update salesOrder: " + salesOrder);
                salesOrderService.createSalesOrder(salesOrder);

                // update PayPalPayment info
                payPalPayment.setPaymentState(PaymentStatus.EXPIRED.name());
                payPalPayment.setStatus(PaymentStatus.EXPIRED.name());
                payPalPayment.setUpdatedAt(now);
                log.info("update payPalPayment: " + payPalPayment);
                payPalPaymentRepository.save(payPalPayment);

                throw new PaymentExpiredException("*ERROR: Payment has been expired!!");
//                return new PaymentResponse("expired", getCreatePayment.getId(), null, null, null);
            }

            // Logic to execute a payment after the user approves it on PayPal's end
            // This typically involves using the PayPal API to execute the payment using the payment ID and payer ID
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            // EXEC payment
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            log.info("executedPayment: " + executedPayment);

            // save the executedPayment response
            PayPalPaymentResponseEntity paymentResponse = saveExecutedPayment(executedPayment);

            // update paypal payment entity status info
            //TODO: 校验state
            payPalPayment.setPaymentState("complete");
            payPalPayment.setStatus(PaymentStatus.SUCCESS.name());
            payPalPayment.setUpdatedAt(now);
            payPalPayment.setPayPalFee(paymentResponse.getSaleTransactionFeeValue());
            payPalPayment.setNet(paymentResponse.getSaleAmountTotal() - paymentResponse.getSaleTransactionFeeValue());
            payPalPayment.setPayerId(paymentResponse.getPayerId());
            payPalPayment.setMerchantId(paymentResponse.getPayeeMerchantId());
            payPalPaymentRepository.save(payPalPayment);
            log.info("update PayPalPayment: " + payPalPayment);

            // update SalesOrder Info
            // get the salesOrderSn (customer)
            String salesOrderSn = extractCustomOrderSn(executedPayment);
            SalesOrder salesOrder = salesOrderService.getSalesOrderBySalesOrderSn(salesOrderSn).getBody();
            // Update the orderStatus
            salesOrder.setOrderStatus(OrderStatus.PROCESSING);
            salesOrder.setLastUpdated(
                    LocalDateTime.parse(
                            executedPayment.getUpdateTime(),
                            DateTimeFormatter.ISO_DATE_TIME)
            );

            log.info("update salesOrder status: " + salesOrder);
            salesOrderService.createSalesOrder(salesOrder);

            // Decrease the Product Inventory
            // Reduce inventory - part of the database transaction
            // get order by salesOrderSn
            List<SalesOrderDetail> salesOrderDetailList = salesOrderDetailService.getSalesOrderDetailsBySalesOrderSn(salesOrderSn).getBody(); //.findSalesOrderDetailsBySalesOrderSn(salesOrderSn);
            for (SalesOrderDetail salesOrderDetail : salesOrderDetailList) {
                String skuCode = salesOrderDetail.getSkuCode();
                int quantity = salesOrderDetail.getQuantity();
                log.info("complete: salesOrderDetails product Info: " + skuCode + " " + quantity);

                // Reduce inventory - part of the database transaction
                productService.reduceProductInventory(skuCode, quantity);

//                // After successful transaction, send message to RabbitMQ
//                rabbitMQSender.sendInventoryReduction(new InventoryUpdateMessage(skuCode, quantity));
            }

            return new PaymentResponse("success", executedPayment.getId(), null, null, null);

        } catch (PaymentExpiredException e) {
//            return paymentErrorHandlingService.handlePaymentExpiredError(e, payPalPayment.getTransactionId(), payPalPayment.getSalesOrderSn());
        } catch (PaymentProcessingException e) {
//            return paymentErrorHandlingService.handlePaymentProcessingError(e, payPalPayment.getTransactionId(), payPalPayment.getSalesOrderSn());
        } catch (PayPalRESTException e) {
//            return paymentErrorHandlingService.handlePayPalRESTError(e, payPalPayment.getTransactionId(), payPalPayment.getSalesOrderSn());
        } catch (Exception e) {
//            return paymentErrorHandlingService.handleGenericError(e, payPalPayment.getTransactionId(), payPalPayment.getSalesOrderSn());
        }
        return null;
    }

    @Override
    public PaymentResponse checkPaymentStatus(String token, String supplierId) throws Exception {

        PayPalPayment payPalPayment = payPalPaymentRepository.findByPaypalToken(token);
        if (payPalPayment == null) {
            return new PaymentResponse("Error", token, "Payment not found for token", null, null);
        }

        String transactionId = payPalPayment.getTransactionId();
//        APIContext apiContext = getAPIContext();
        APIContext apiContext = createApiContextForSupplier(Long.valueOf(supplierId));

        Payment payment = null;

        try {

            // get payment through transactionId
            payment = Payment.get(apiContext, transactionId);
            log.info("***transactionId: "+ transactionId);

            // check if salesOrder is expired
            String expiredTime = extractDescription(payment);
            // convert string into Date time & LocalTime
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime time = LocalDateTime.parse(expiredTime, formatter);

            // check if the payment has expired
            if (time.isBefore(LocalDateTime.now())) {
                // update SalesOrder Status to EXPIRED
                // get salesOrderSn
                String salesOrderSn = extractCustomOrderSn(payment);
                SalesOrder salesOrder = salesOrderService.getSalesOrderBySalesOrderSn(salesOrderSn).getBody(); // salesOrderRepository.getSalesOrderBySalesOrderSn(salesOrderSn).get();
                payment.setState(OrderStatus.EXPIRED.name());
                payPalPayment.setPaymentState(PaymentStatus.EXPIRED.name());
                salesOrder.setOrderStatus(OrderStatus.EXPIRED);
                payPalPaymentRepository.save(payPalPayment);
                salesOrderService.createSalesOrder(salesOrder); // salesOrderRepository.save(salesOrder);

                throw new PaymentExpiredException("ERROR: Payment has been expired!", ErrorCategory.ORDER_EXPIRED);
            }

            double amount = Double.parseDouble(payment.getTransactions().get(0).getAmount().getTotal());

            if (!payment.getState().equals("complete")) {
                // payment rollback, throw the error
                throw new PaymentProcessingException("*ERROR: Payment creation interrupted because the customer exited.", ErrorCategory.CLIENT_EXIT);
            }
            return new PaymentResponse(payment.getState(), token, "amount: " + amount, null, null);

        } catch (PaymentExpiredException e) {
//            return paymentErrorHandlingService.handlePaymentExpiredError(e, transactionId, payPalPayment.getSalesOrderSn());
        } catch (PaymentProcessingException e) {
//            return paymentErrorHandlingService.handlePaymentProcessingError(e, transactionId, payPalPayment.getSalesOrderSn());
        } catch (PayPalRESTException e) {
//            return paymentErrorHandlingService.handlePayPalRESTError(e, transactionId, payPalPayment.getSalesOrderSn());
        } catch (Exception e) {
//            return paymentErrorHandlingService.handleGenericError(e, transactionId, payPalPayment.getSalesOrderSn());
        }
        return null;
    }

    private PayPalPaymentResponseEntity saveExecutedPayment(Payment executedPayment) {
        try{
            PayPalPaymentResponseEntity paymentEntity = new PayPalPaymentResponseEntity();

            // Basic payment info
            paymentEntity.setPaymentId(executedPayment.getId());
            paymentEntity.setIntent(executedPayment.getIntent());
            paymentEntity.setCart(executedPayment.getCart());
            paymentEntity.setState(executedPayment.getState());
            paymentEntity.setCreateTime(executedPayment.getCreateTime());
            paymentEntity.setUpdateTime(executedPayment.getUpdateTime());

            // Payer info
            Payer payer = executedPayment.getPayer();
            PayerInfo payerInfo = payer.getPayerInfo();
            paymentEntity.setPayerStatus(payer.getStatus());
            paymentEntity.setPayerEmail(payerInfo.getEmail());
            paymentEntity.setPayerFirstName(payerInfo.getFirstName());
            paymentEntity.setPayerLastName(payerInfo.getLastName());
            paymentEntity.setPayerId(payerInfo.getPayerId());
            paymentEntity.setPayerCountryCode(payerInfo.getCountryCode());

            // Shipping address
            if (payerInfo.getShippingAddress() != null) {
                paymentEntity.setShippingRecipientName(payerInfo.getShippingAddress().getRecipientName());
                paymentEntity.setShippingLine1(payerInfo.getShippingAddress().getLine1());
                paymentEntity.setShippingCity(payerInfo.getShippingAddress().getCity());
                paymentEntity.setShippingState(payerInfo.getShippingAddress().getState());
                paymentEntity.setShippingCountryCode(payerInfo.getShippingAddress().getCountryCode());
                paymentEntity.setShippingPostalCode(payerInfo.getShippingAddress().getPostalCode());
            }

            // Assuming only one transaction for simplicity
            Transaction transaction = executedPayment.getTransactions().get(0);
//            log.info("transaction: " + transaction + "; custom: " + transaction.getCustom() + "; " + transaction.getDescription());
            Amount transactionAmount = transaction.getAmount();
            paymentEntity.setTransactionAmountCurrency(transactionAmount.getCurrency());
            paymentEntity.setTransactionAmountTotal(Double.parseDouble(transactionAmount.getTotal()));
            paymentEntity.setTransactionDescription(transaction.getDescription());
            paymentEntity.setTransactionCustom(transaction.getCustom());
            paymentEntity.setTransactionSoftDescriptor(transaction.getSoftDescriptor());

            // Payee info
            Payee payee = transaction.getPayee();
            paymentEntity.setPayeeEmail(payee.getEmail());
            paymentEntity.setPayeeMerchantId(payee.getMerchantId());

            // Sale info
            List<RelatedResources> relatedResources = transaction.getRelatedResources();
            if (!relatedResources.isEmpty()) {
                Sale sale = relatedResources.get(0).getSale();
                paymentEntity.setSaleId(sale.getId());
                paymentEntity.setSaleState(sale.getState());
                paymentEntity.setSalePaymentMode(sale.getPaymentMode());
                paymentEntity.setSaleProtectionEligibility(sale.getProtectionEligibility());
                paymentEntity.setSaleProtectionEligibilityType(sale.getProtectionEligibilityType());
                paymentEntity.setSaleCreateTime(sale.getCreateTime());
                paymentEntity.setSaleUpdateTime(sale.getUpdateTime());

                Amount saleAmount = sale.getAmount();
                paymentEntity.setSaleAmountCurrency(saleAmount.getCurrency());
                paymentEntity.setSaleAmountTotal(Double.parseDouble(saleAmount.getTotal()));

                Details details = saleAmount.getDetails();
                paymentEntity.setSaleAmountDetailsSubtotal(details.getSubtotal());
                paymentEntity.setSaleAmountDetailsShipping(details.getShipping());
                paymentEntity.setSaleAmountDetailsHandlingFee(details.getHandlingFee());
                paymentEntity.setSaleAmountDetailsShippingDiscount(details.getShippingDiscount());
                paymentEntity.setSaleAmountDetailsInsurance(details.getInsurance());

                if (sale.getTransactionFee() != null) {
                    paymentEntity.setSaleTransactionFeeCurrency(sale.getTransactionFee().getCurrency());
                    paymentEntity.setSaleTransactionFeeValue(Double.parseDouble(sale.getTransactionFee().getValue()));
                }
            }

            log.info("successfully save the executedPayment!");

            // Save to repository
            return payPalPaymentResponseRepo.save(paymentEntity);

        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    private String extractPaymentId(Payment payment) {
        return payment.getId();
    }

    private String extractPaymentStatus(Payment payment) {
        return payment.getState();
    }

    private String extractApprovalUrl(Payment payment) {

        return payment.getLinks().stream()
                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))  // approval_url  execute
                .findFirst()
                .map(link -> link.getHref())
                .orElse(null);
    }

    private String extractToken(String approvalUrl) {
        // Extract the token from the approval URL
        try {
            URI uri = new URI(approvalUrl);
            Map<String, List<String>> queryPairs = Stream.of(uri.getQuery().split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.groupingBy(s -> s[0],
                            Collectors.mapping(s -> s.length > 1 ? s[1] : "", Collectors.toList())));

            List<String> tokens = queryPairs.get("token");
            return tokens != null && !tokens.isEmpty() ? tokens.get(0) : null;
        } catch (URISyntaxException e) {
            log.error("Error parsing approval URL: ", e);
            return null;
        }
    }

    private String extractDescription(Payment payment) {
        return payment.getTransactions().stream()
                .filter(Objects::nonNull)
                .map(Transaction::getDescription)
                .findFirst()
                .orElse(null);
    }

    private String extractCustomOrderSn(Payment payment) {
        return payment.getTransactions().stream()
                .filter(Objects::nonNull)
                .map(Transaction::getCustom)
                .findFirst()
                .orElse(null);
    }

    private boolean customerHasInsufficientFunds(Long customerId, BigDecimal amount) {
        // Logic to check if the customer's account balance is sufficient for the payment
        // Return true if the customer has insufficient funds, and false otherwise
        // For demonstration purposes, we assume the customer has insufficient funds if the amount is greater than $1000
        return amount.compareTo(new BigDecimal("1000")) > 0;
    }

    // Additional method to simulate payment interruption during payment creation
    private boolean customerExitsDuringPayment() {
        // Logic to determine if payment creation should be interrupted
        // Return true if payment should be interrupted, and false otherwise
        // For demonstration purposes, we assume payment is interrupted if a certain condition is met
        return false;  // true false  Math.random() < 0.9
    }

}
