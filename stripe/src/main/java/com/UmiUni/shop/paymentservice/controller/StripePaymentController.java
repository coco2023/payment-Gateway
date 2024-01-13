package com.UmiUni.shop.paymentservice.controller;

import com.UmiUni.shop.paymentservice.model.StripePaymentRequest;
import com.UmiUni.shop.paymentservice.model.StripePaymentResponse;
import com.UmiUni.shop.paymentservice.service.StripeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
public class StripePaymentController {

    @Autowired
    private StripeService stripeService;

    /**
     * Stripe Payment
     * http://localhost:9012/api/v1/payments/stripe/charge
     */
    @PostMapping("/stripe/charge")
    public ResponseEntity<?> createCharge(@RequestBody StripePaymentRequest request) {
        try {
            StripePaymentResponse response = stripeService.createCharge(request.getSalesOrder(), request.getToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " + e.getMessage());
        }
    }


}
