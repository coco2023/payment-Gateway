package com.UmiUni.shop.paymentservice.model;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class KlarnaPaymentRequest {

    private BigDecimal amount;

    private String currency;

}
