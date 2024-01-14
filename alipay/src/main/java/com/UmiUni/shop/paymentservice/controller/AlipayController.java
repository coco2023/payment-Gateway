package com.UmiUni.shop.paymentservice.controller;

import com.UmiUni.shop.paymentservice.model.AlipayRequest;
import com.UmiUni.shop.paymentservice.model.AlipayResponse;
import com.UmiUni.shop.paymentservice.service.AlipayService;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alipay")
@CrossOrigin(origins = "http://localhost:3000")
public class AlipayController {

    @Autowired
    private AlipayService alipayService;
//
//    // http://localhost:9013/api/alipay/create-payment
//    @PostMapping("/create-payment")
//    public ResponseEntity<?> createPayment(@RequestBody AlipayRequest alipayRequest) throws AlipayApiException {
//        try {
//            String paymentLink = alipayService.createPayment(alipayRequest);
//            return ResponseEntity.ok(paymentLink);
////            return ResponseEntity.ok().body(Map.of("paymentUrl", paymentLink));
//        } catch (AlipayApiException e) {
//            // Specific exception handling for Alipay API exceptions
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            // General exception handling for other unexpected exceptions
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
//        }
//    }

    // http://localhost:9013/api/alipay/create-payment
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody AlipayRequest alipayRequest) throws AlipayApiException {
        try {
            AlipayResponse alipayResponse = alipayService.createPayment(alipayRequest);
            return ResponseEntity.ok(alipayResponse);
//            return ResponseEntity.ok().body(Map.of("paymentUrl", paymentLink));
        } catch (AlipayApiException e) {
            // Specific exception handling for Alipay API exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // General exception handling for other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // http://localhost:9013/api/alipay/process-payment
    @PostMapping("/process-response")
    public ResponseEntity<AlipayResponse> processResponse(@RequestBody Map<String, String> params) {
        try {
            AlipayResponse response = alipayService.processResponse(params);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exception and return an appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AlipayResponse(e.getMessage()));
        }
    }
}

