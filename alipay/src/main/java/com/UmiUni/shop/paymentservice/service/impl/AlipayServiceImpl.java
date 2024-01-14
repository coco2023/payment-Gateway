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

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.signType}")
    private String signType;

    @Autowired
    private AlipayClient alipayClient;

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
            response.setSalesOrderSn(request.getSalesOrderSn());
            response.setAmount(request.getAmount());
            response.setCurrency(request.getCurrency());
            response.setTransactionStatus(PaymentStatus.CREATE.name());
            response.setMessage("payment created!");

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
