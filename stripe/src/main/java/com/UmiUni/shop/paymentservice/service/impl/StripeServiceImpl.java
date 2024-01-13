package com.UmiUni.shop.paymentservice.service.impl;

import com.UmiUni.shop.paymentservice.constant.PaymentStatus;
import com.UmiUni.shop.paymentservice.entity.StripePayment;
import com.UmiUni.shop.paymentservice.model.SalesOrder;
import com.UmiUni.shop.paymentservice.model.StripePaymentResponse;
import com.UmiUni.shop.paymentservice.repository.StripePaymentRepository;
import com.UmiUni.shop.paymentservice.service.StripeService;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String apiKey;

    @Autowired
    private StripePaymentRepository stripePaymentRepository;

    @Override
    public StripePaymentResponse createCharge(SalesOrder salesOrder, String token) {

        try{

            Stripe.apiKey = apiKey;

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", salesOrder.getTotalAmount().multiply(new BigDecimal(100)).intValue()); // Convert to cents
            chargeParams.put("currency", "usd"); // Assuming USD, modify as necessary
            chargeParams.put("source", token); // Token obtained from Stripe.js on the frontend
            chargeParams.put("description", "Charge for order " + salesOrder.getSalesOrderSn());
            chargeParams.put("receipt_email", salesOrder.getCustomerEmail()); // Customer's email

            // Optional: Add metadata for tracking
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sales_order", salesOrder);
            metadata.put("customer_id", salesOrder.getCustomerId().toString());
            metadata.put("shipping_address", salesOrder.getShippingAddress());
            metadata.put("billing_address", salesOrder.getBillingAddress());
            chargeParams.put("metadata", metadata);

            Charge charge = Charge.create(chargeParams);

            // Save or update payment details in the database
            StripePayment payment = new StripePayment();
            payment.setTransactionId(charge.getId());
            payment.setSalesOrderSn(salesOrder.getSalesOrderSn());
            payment.setCurrency("USD");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCustomerEmail(salesOrder.getCustomerEmail());
            payment.setPaymentStatus(mapStripeStatusToPaymentStatus(charge.getStatus()));
            payment.setPaymentMethod(salesOrder.getPaymentMethod()); // Since Stripe is used here
            payment.setPaymentMethodDetails(charge.getPaymentMethodDetails().getType());
            payment.setAmount(salesOrder.getTotalAmount());
            payment.setReceiptUrl(charge.getReceiptUrl()); // Set the receipt URL

            payment.setErrorMessage(charge.getFailureMessage()); // In case of failure
            stripePaymentRepository.save(payment);

            return new StripePaymentResponse(charge.getStatus(), charge.getId(), null, null, null );

        } catch (Exception e) {
            e.printStackTrace();
            return new StripePaymentResponse("failed", null, null, e.getMessage(), null);
        }
    }

    private PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        // Map Stripe's charge status to your PaymentStatus enum
        switch (stripeStatus) {
            case "succeeded":
                return PaymentStatus.SUCCESS;
            case "failed":
                return PaymentStatus.FAILED;
            default:
                return PaymentStatus.PENDING;
        }
    }

}
