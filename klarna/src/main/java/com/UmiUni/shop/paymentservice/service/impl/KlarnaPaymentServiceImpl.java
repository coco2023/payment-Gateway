package com.UmiUni.shop.paymentservice.service.impl;

import com.UmiUni.shop.paymentservice.model.KlarnaPaymentRequest;
import com.UmiUni.shop.paymentservice.model.KlarnaPaymentResponse;
import com.UmiUni.shop.paymentservice.service.KlarnaPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class KlarnaPaymentServiceImpl implements KlarnaPaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Value(value = "${klarna.create-session}")
    private String klarnaUrl;

    @Value(value = "${klarna.password}")
    private String password;

//    @Autowired
//    private WebClient webClient;

    public KlarnaPaymentResponse createPayment(KlarnaPaymentRequest request) {
        String url = klarnaUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Add any other necessary headers, like authorization
        headers.set("Authorization", "Basic " + password);

        HttpEntity<KlarnaPaymentRequest> entity = new HttpEntity<>(request, headers);

        KlarnaPaymentResponse response = restTemplate.postForEntity(url, entity, KlarnaPaymentResponse.class).getBody();
        return response;
    }

//    public Mono<KlarnaResponse> createPayment(KlarnaPaymentRequest request) {
//        return webClient.post()
//                .uri("/payments/v1/sessions")
//                .contentType(MediaType.APPLICATION_JSON)
//                // Add any other necessary headers, like authorization
//                .body(Mono.just(request), KlarnaPaymentRequest.class)
//                .retrieve()
//                .bodyToMono(KlarnaResponse.class);
//    }
//

}
