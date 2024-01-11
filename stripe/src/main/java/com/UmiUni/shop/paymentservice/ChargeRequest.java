package com.UmiUni.shop.paymentservice;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class ChargeRequest {

    private int amount; // The charge amount in cents
    private String currency; // The currency code, e.g., "usd"
    private String source; // The ID of the source (e.g., a credit card token)
    private String description; // A description for the charge


}
