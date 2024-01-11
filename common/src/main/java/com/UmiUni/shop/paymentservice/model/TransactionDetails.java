package com.UmiUni.shop.paymentservice.model;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class TransactionDetails {
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String productDescription;

}
