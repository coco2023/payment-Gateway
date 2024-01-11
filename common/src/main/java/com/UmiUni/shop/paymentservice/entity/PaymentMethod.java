package com.UmiUni.shop.paymentservice.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Long id;

    @Column(name = "method_name", nullable = false, length = 50)
    private String methodName;

    @Column(columnDefinition = "TEXT")
    private String details;

}
