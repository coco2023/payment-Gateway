package com.UmiUni.shop.paymentservice.repository;

import com.UmiUni.shop.paymentservice.entity.StripePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripePaymentRepository extends JpaRepository<StripePayment, Long> {
}
