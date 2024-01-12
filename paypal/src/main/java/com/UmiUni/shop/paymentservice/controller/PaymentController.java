package com.UmiUni.shop.paymentservice.controller;

import com.UmiUni.shop.paymentservice.model.PaymentResponse;
import com.UmiUni.shop.paymentservice.model.SalesOrder;
import com.UmiUni.shop.paymentservice.service.PayPalService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Log4j2
public class PaymentController {

    @Autowired
    private PayPalService payPalService;

    // Endpoint to create a payment  localhost:9011/api/v1/payments/paypal/create
    @PostMapping("/paypal/create")
    public ResponseEntity<?> createPayment(@RequestBody SalesOrder salesOrder) {
        try {
            PaymentResponse paymentResponse = payPalService.createPayment(salesOrder);
            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating PayPal payment: " + e.getMessage());
        }
    }

    // Endpoint to complete a payment
    @PostMapping("/paypal/complete")
    public ResponseEntity<?> completePayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @RequestParam("SupplierId") String supplierId) {
        try {
            PaymentResponse paymentResponse = payPalService.completePayment(paymentId, payerId, supplierId);
            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error completing PayPal payment: " + e.getMessage());
        }
    }

}
