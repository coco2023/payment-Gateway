package com.UmiUni.shop.paymentservice.service;

import com.UmiUni.shop.paymentservice.model.KlarnaPaymentRequest;
import com.UmiUni.shop.paymentservice.model.KlarnaPaymentResponse;

public interface KlarnaPaymentService {

    public KlarnaPaymentResponse createPayment(KlarnaPaymentRequest request);
}
