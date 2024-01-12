package com.UmiUni.shop.paymentservice;


import com.UmiUni.shop.paymentservice.model.TransactionDetails;

public interface PayPalServiceTest {
    String createTransaction(TransactionDetails details);
    // Define additional methods as needed (e.g., complete payment, refund)
}
