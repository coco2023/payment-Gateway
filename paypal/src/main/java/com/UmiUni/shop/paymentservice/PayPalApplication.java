package com.UmiUni.shop.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PayPalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayPalApplication.class, args);
    }

}
