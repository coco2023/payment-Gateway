package com.UmiUni.shop.paymentservice;

import com.UmiUni.shop.paymentservice.model.TransactionDetails;

public interface AliPayService {
    String createTransaction(TransactionDetails details);
    // Define additional methods as needed
}
