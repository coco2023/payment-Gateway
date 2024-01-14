package com.UmiUni.shop.paymentservice.controller;

import com.UmiUni.shop.paymentservice.entity.KlarnaPayment;
import com.UmiUni.shop.paymentservice.model.KlarnaPaymentRequest;
import com.UmiUni.shop.paymentservice.model.KlarnaPaymentResponse;
import com.UmiUni.shop.paymentservice.service.KlarnaPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class KlarnaPaymentController {

    @Autowired
    private KlarnaPaymentService klarnaPaymentService;

    // http://localhost:9014/api/payments/klarna/create-payment
    @PostMapping("/klarna/create-payment")
    public ResponseEntity<KlarnaPaymentResponse> createPayment(@RequestBody KlarnaPaymentRequest request) {
        KlarnaPaymentResponse response = klarnaPaymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }


//    @PostMapping
//    public ResponseEntity<KlarnaPayment> createPayment(@RequestBody KlarnaPaymentRequest request) {
//        KlarnaPayment payment = paymentService.createPayment(request.getAmount(), request.getCurrency());
//        return new ResponseEntity<>(payment, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<KlarnaPayment> getPayment(@PathVariable Long id) {
//        KlarnaPayment payment = paymentService.getPaymentDetails(id);
//        return ResponseEntity.ok(payment);
//    }

}
