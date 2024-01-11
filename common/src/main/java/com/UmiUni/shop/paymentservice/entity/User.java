//package com.UmiUni.shop.paymentservice.entity;
//
//import javax.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//@Builder
//@ToString
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
//    private Long id;
//
//    @Column(nullable = false, length = 50)
//    private String username;
//
//    @Column(nullable = false, unique = true, length = 100)
//    private String email;
//
//    @Column(name = "password_hash", nullable = false, length = 255)
//    private String passwordHash;
//
//    @Column(name = "created_at", nullable = false)
////    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//}
