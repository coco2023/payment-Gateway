package com.UmiUni.shop.paymentservice.service;

import com.UmiUni.shop.paymentservice.model.SalesOrder;
import com.UmiUni.shop.paymentservice.model.StripePaymentResponse;

public interface StripeService {

    public StripePaymentResponse createCharge(SalesOrder salesOrder, String token);

}
