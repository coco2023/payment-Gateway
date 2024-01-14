package com.UmiUni.shop.paymentservice.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Table(name = "Ali_Payment")
public class AliPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String outTradeNo; // Unique transaction ID

    private BigDecimal amount; // Transaction amount

    private String currency; // Currency type, e.g., CNY

    private String paymentStatus; // Status of the payment

}
