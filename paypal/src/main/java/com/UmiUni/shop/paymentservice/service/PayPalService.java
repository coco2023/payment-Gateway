package com.UmiUni.shop.paymentservice.service;

import com.UmiUni.shop.paymentservice.model.PaymentResponse;
import com.UmiUni.shop.paymentservice.model.SalesOrder;

public interface PayPalService {
    PaymentResponse createPayment(SalesOrder salesOrder);

    PaymentResponse completePayment(String paymentId, String payerId, String supplierId);

    PaymentResponse checkPaymentStatus(String token, String supplierId) throws Exception;

}
