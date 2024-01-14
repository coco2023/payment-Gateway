package com.UmiUni.shop.paymentservice.component;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AlipayClientWrapper {

    private AlipayClient alipayClient;

    @Value("${alipay.api-url}")
    private String gatewayUrl;

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.merchant-private-key}")
    private String privateKey;

    @Value("${alipay.alipay-public-key}")
    private String publicKey;

    @Value("${alipay.format}")
    private String format;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.signType}")
    private String signType;

    public AlipayClientWrapper() {
        // Initialize the Alipay client
        this.alipayClient = new DefaultAlipayClient(
                gatewayUrl, appId, privateKey, "json", "UTF-8", publicKey, "RSA2"
        );
    }

    public String createPayment(String outTradeNo, double totalAmount, String subject) throws AlipayApiException {
        // Create a request object
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // Set business parameters
        request.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        // Execute the request
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request); // execute(request);
        if (response.isSuccess()) {
            return response.getBody(); // The form for Alipay payment page
        } else {
            throw new AlipayApiException("Failed to create Alipay payment: " + response.getSubMsg());
        }
    }

    public boolean verifyResponse(Map<String, String> params) throws AlipayApiException {
        // Implement the logic to verify the response from Alipay
        try {
            // Assuming params is a map containing the response from Alipay
            // Signature verification
            return AlipaySignature.rsaCheckV1(params, publicKey, charset, signType);
        } catch (AlipayApiException e) {
            // Log and handle exception
            throw new AlipayApiException("Failed to verify Alipay response", e);
        }
    }

}
