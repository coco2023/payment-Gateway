package com.UmiUni.shop.paymentservice.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.api-url}")
    private String gatewayUrl;

    @Value("${alipay.format}")
    private String format;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.sign-type}")
    private String signType;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        return new DefaultAlipayClient(
                gatewayUrl,
                appId,
                merchantPrivateKey,
                format,
                charset,
                alipayPublicKey,
                signType
        );
    }
}
