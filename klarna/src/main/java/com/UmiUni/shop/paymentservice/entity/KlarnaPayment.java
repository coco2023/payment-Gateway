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
@Table(name = "Klarna_Payment")
public class KlarnaPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String klarnaOrderId;
    private BigDecimal amount;
    private String currency;
    private String status;
}
