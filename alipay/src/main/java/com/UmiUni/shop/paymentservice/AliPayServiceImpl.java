package com.UmiUni.shop.paymentservice;

import com.UmiUni.shop.paymentservice.model.TransactionDetails;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AliPayServiceImpl implements AliPayService {

    @Value("${alipay.api-url}")
    private String apiUrl;

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    // Constructor, autowire any needed repositories

    @Override
    public String createTransaction(TransactionDetails details) {
        AlipayClient alipayClient = new DefaultAlipayClient(apiUrl, appId, merchantPrivateKey, "json", "UTF-8", alipayPublicKey, "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // Set request parameters based on TransactionDetails

        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                return "Success: " + response.getBody(); // Or handle as needed
            } else {
                return "Failed to create transaction";
            }
        } catch (Exception e) {
            // Handle exception
            return "Error: " + e.getMessage();
        }
    }

    // Implement other methods
}
