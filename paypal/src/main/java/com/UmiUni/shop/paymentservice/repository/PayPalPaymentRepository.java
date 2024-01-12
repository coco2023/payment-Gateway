package com.UmiUni.shop.paymentservice.repository;

import com.UmiUni.shop.paymentservice.entity.PayPalPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayPalPaymentRepository extends JpaRepository<PayPalPayment, Long> {

    // Method to find a payment by its PayPal token
    PayPalPayment findByPaypalToken(String paypalToken);

}
