package com.UmiUni.shop.paymentservice;

public interface StripeServiceTest {
    String createCharge(ChargeRequest chargeRequest);
    // Additional methods as needed (e.g., refunds, subscriptions)
}
