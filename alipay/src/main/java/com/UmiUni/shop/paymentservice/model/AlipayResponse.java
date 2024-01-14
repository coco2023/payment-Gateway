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

//    private String transactionId;

    private String salesOrderSn;

    private BigDecimal amount;

    private String currency;

    private String message;


    private String outTradeNo;  // important! UUID

    private String paymentUrl; // URL to redirect user for payment

    public AlipayResponse(String message) {
        this.message = message;
    }
}
