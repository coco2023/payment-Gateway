package com.UmiUni.shop.paymentservice;

public interface StripeService {
    String createCharge(ChargeRequest chargeRequest);
    // Additional methods as needed (e.g., refunds, subscriptions)
}
