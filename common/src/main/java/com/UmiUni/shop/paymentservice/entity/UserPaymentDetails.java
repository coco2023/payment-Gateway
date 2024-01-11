package com.UmiUni.shop.paymentservice.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Table(name = "user_payment_details")
public class UserPaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "method_id", nullable = false)
    private Long methodId;

    @Column(columnDefinition = "TEXT")
    private String details;

}
