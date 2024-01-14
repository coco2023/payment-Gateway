package com.UmiUni.shop.paymentservice.model;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class AlipayRequest {

    private BigDecimal amount;

    private String orderNumber;

    private String currency;

}
