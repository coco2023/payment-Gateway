package com.UmiUni.shop.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stripe")
public class StripeControllerTest {

    @Autowired
    private StripeServiceTest stripeService;

    @PostMapping("/charge")
    public String createCharge(@RequestBody ChargeRequest chargeRequest) {
        // Assume ChargeRequest is a class containing necessary information like amount, currency, etc.
        return stripeService.createCharge(chargeRequest);
    }

    // Additional endpoints as needed
}
