package com.UmiUni.shop.paymentservice.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class KlarnaPaymentResponse {

    private String sessionId; // An identifier for the payment session

    private String status; // The status of the payment session

    private String redirectUrl; // A URL to redirect the user for completing the payment, if applicable

}
