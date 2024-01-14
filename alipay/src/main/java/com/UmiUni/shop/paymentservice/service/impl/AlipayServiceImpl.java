package com.UmiUni.shop.paymentservice.service.impl;

import com.UmiUni.shop.paymentservice.constant.PaymentStatus;
import com.UmiUni.shop.paymentservice.model.AlipayRequest;
import com.UmiUni.shop.paymentservice.model.AlipayResponse;
import com.UmiUni.shop.paymentservice.service.AlipayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Value("${alipay.api-url}")
    private String apiUrl;

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.format}")
    private String format;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.signType}")
    private String signType;

    @Autowired
    private AlipayClient alipayClient;

////    @Override
//    public String createPayment2(AlipayRequest alipayRequest) throws AlipayApiException {
//        AlipayClient alipayClient = new DefaultAlipayClient(apiUrl, appId, merchantPrivateKey, "json", "UTF-8", alipayPublicKey, "RSA2");
//        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        // Set request parameters based on TransactionDetails
//
//        try {
//            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
//            if (response.isSuccess()) {
//                return "Success: " + response.getBody(); // Or handle as needed
//            } else {
//                return "Failed to create transaction";
//            }
//        } catch (Exception e) {
//            // Handle exception
//            return "Error: " + e.getMessage();
//        }
//    }

    @Override
    public AlipayResponse createPayment(AlipayRequest request) throws AlipayApiException {
        // Generate a unique trade number
        String outTradeNo = generateUniqueTradeNo();
        // Create the BizContent object
        String bizContent = "{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "\"total_amount\":" + request.getAmount().toString() + "," +
                "\"subject\":\"Your Product Name\"" +
                "}";

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setBizContent(bizContent);
//        alipayRequest.setReturnUrl("localhost:3000/payment-success");
//        alipayRequest.setNotifyUrl("your-notify-url");

        try {
            String result = alipayClient.pageExecute(alipayRequest).getBody();

            AlipayResponse response = new AlipayResponse();
            // Set response details
            response.setOutTradeNo(outTradeNo);
            response.setPaymentUrl(result); // URL to redirect user for payment
            response.setOrderNumber(request.getOrderNumber());
            response.setAmount(request.getAmount());
            response.setTransactionStatus(PaymentStatus.CREATE.name());

            return response;
        } catch (AlipayApiException e) {
            // Handle exception
            throw new RuntimeException(e);
        }
    }

    private String generateUniqueTradeNo() {
        // Generate a unique trade number
        return UUID.randomUUID().toString();
    }

    @Override
    public AlipayResponse processResponse(Map<String, String> params) {
        try {
            // Verify the response from Alipay
            boolean isResponseValid = verifyResponse(params);
            if (!isResponseValid) {
                throw new RuntimeException("Invalid response from Alipay");
            }

            // Directly extract and process necessary information from params
//            String tradeStatus = params.get("trade_status");
            String orderSn = params.get("order_sn");
            String orderNumber = params.get("out_trade_no");
            String totalAmount = params.get("total_amount"); // assuming this field is present
//            String message = params.get("msg"); // assuming this field is present
            AlipayResponse response = new AlipayResponse();
//            response.setTransactionStatus(tradeStatus);
            response.setOrderNumber(orderNumber);
            response.setOutTradeNo(orderSn);
            response.setAmount(totalAmount != null ? new BigDecimal(totalAmount) : null);
//            response.setMessage(message);

            return response;
        } catch (Exception e) {
            // Handle exception
            throw new RuntimeException("Error processing Alipay response: " + e.getMessage(), e);
        }
    }

//    @Override
//    public String createPayment1(AlipayRequest alipayRequest) throws AlipayApiException {
//        try {
//            // Use the order number and amount from the AlipayRequest
//            String outTradeNo = alipayRequest.getOrderNumber();
//            double totalAmount = alipayRequest.getAmount();
//            String subject = "Order Payment";
//
//            return alipayClientWrapper.createPayment(outTradeNo, totalAmount, subject);
//        } catch (AlipayApiException e) {
//            throw new RuntimeException("Failed to create Alipay payment", e);
//        }
//    }

//    @Override
//    public AlipayResponse processResponse(Map<String, String> params) {
//        try {
//            // Verify the response from Alipay
//            boolean isResponseValid = alipayClientWrapper.verifyResponse(params);
//            if (!isResponseValid) {
//                throw new RuntimeException("Invalid response from Alipay");
//            }
//
//            // Extract necessary information from the verified response
//            AlipayResponse response = new AlipayResponse();
//            response.setTransactionStatus(params.get("trade_status")); // Example key, adjust based on actual response
////            response.setTransactionId(params.get("trade_status")); // Example key, adjust based on actual response
//            response.setOrderNumber(params.get("out_trade_no")); // Example key, adjust based on actual response
//            response.setAmount(Double.parseDouble(params.get("total_amount"))); // Example key, adjust based on actual response
//            response.setMessage(params.get("msg")); // Example key, adjust based on actual response
//            // For example, extract transaction status, order number, etc.
//            // response.setTransactionStatus(params.get("..."));
//            // response.setOrderNumber(params.get("..."));
//            // ... other response handling
//
//            return response;
//        } catch (AlipayApiException e) {
//            // Handle Alipay API specific exceptions
//            throw new RuntimeException("Error processing Alipay response", e);
//        } catch (Exception e) {
//            // Handle other exceptions
//            throw new RuntimeException("Error in processing response", e);
//        }
//    }

    public boolean verifyResponse(Map<String, String> params) throws AlipayApiException {
        // Implement the logic to verify the response from Alipay
        try {
            // Assuming params is a map containing the response from Alipay
            // Signature verification
            return AlipaySignature.rsaCheckV1(params, alipayPublicKey, charset, signType);
        } catch (AlipayApiException e) {
            // Log and handle exception
            throw new AlipayApiException("Failed to verify Alipay response", e);
        }
    }

}
