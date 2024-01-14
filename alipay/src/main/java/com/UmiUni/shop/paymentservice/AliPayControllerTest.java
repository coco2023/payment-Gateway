package com.UmiUni.shop.paymentservice;

import com.UmiUni.shop.paymentservice.model.TransactionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alipay")
public class AliPayControllerTest {

    private final AliPayServiceTest aliPayService;

    @Autowired
    public AliPayControllerTest(AliPayServiceTest aliPayService) {
        this.aliPayService = aliPayService;
    }

    @PostMapping("/transaction")
    public String createTransaction(@RequestBody TransactionDetails details) {
        return aliPayService.createTransaction(details);
    }

    // Additional endpoints as needed
}
