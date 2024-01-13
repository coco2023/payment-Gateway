package com.UmiUni.shop.paymentservice.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class StripePaymentResponse { // complete payment

    private String status;

    private String transactionId;

    private String description;

    private String errorMesg;

    private String approvalUrl;

}
