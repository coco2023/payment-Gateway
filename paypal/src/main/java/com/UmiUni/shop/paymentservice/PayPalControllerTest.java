package com.UmiUni.shop.paymentservice;

import com.UmiUni.shop.paymentservice.model.TransactionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paypal")
public class PayPalControllerTest {

    private final PayPalServiceTest payPalService;

    @Autowired
    public PayPalControllerTest(PayPalServiceTest payPalService) {
        this.payPalService = payPalService;
    }

    @PostMapping("/transaction")
    public String createTransaction(@RequestBody TransactionDetails details) {
        return payPalService.createTransaction(details);
    }

    // Additional endpoints like handling PayPal webhook notifications
}
