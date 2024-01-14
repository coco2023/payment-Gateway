package com.UmiUni.shop.paymentservice.repository;

import com.UmiUni.shop.paymentservice.entity.KlarnaPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlarnaPaymentRepository extends JpaRepository<KlarnaPayment, Long> {
}
