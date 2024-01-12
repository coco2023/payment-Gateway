package com.UmiUni.shop.paymentservice;

import com.UmiUni.shop.paymentservice.model.TransactionDetails;
import org.springframework.stereotype.Service;

@Service
public class PayPalServiceImplTest implements PayPalServiceTest {

    @Override
    public String createTransaction(TransactionDetails details) {
        // Logic to create a transaction with PayPal
        // This might involve calling the PayPal API and handling the response
        return "Transaction Initiated with PayPal";
    }

}
