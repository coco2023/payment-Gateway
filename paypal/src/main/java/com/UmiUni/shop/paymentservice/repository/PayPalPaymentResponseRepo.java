package com.UmiUni.shop.paymentservice.repository;

import com.UmiUni.shop.paymentservice.entity.PayPalPaymentResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayPalPaymentResponseRepo extends JpaRepository<PayPalPaymentResponseEntity, Long> {
}
