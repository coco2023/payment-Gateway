package com.UmiUni.shop.paymentservice.model;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class AlipayResponse {

    private String transactionStatus;

    private String transactionId;

    private String orderNumber;

    private BigDecimal amount;

    private String message;


    private String outTradeNo;

    private String paymentUrl; // URL to redirect user for payment

    public AlipayResponse(String message) {
        this.message = message;
    }
}
