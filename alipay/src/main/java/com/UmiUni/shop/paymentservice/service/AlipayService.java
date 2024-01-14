package com.UmiUni.shop.paymentservice.service;

import com.UmiUni.shop.paymentservice.model.AlipayRequest;
import com.UmiUni.shop.paymentservice.model.AlipayResponse;
import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface AlipayService {
    public AlipayResponse createPayment(AlipayRequest alipayRequest) throws AlipayApiException;

    public AlipayResponse processResponse(Map<String, String> params);

}
