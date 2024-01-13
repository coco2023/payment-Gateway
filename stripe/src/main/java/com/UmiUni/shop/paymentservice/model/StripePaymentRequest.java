package com.UmiUni.shop.paymentservice.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class StripePaymentRequest {

    private String token;

    private SalesOrder salesOrder;

}
