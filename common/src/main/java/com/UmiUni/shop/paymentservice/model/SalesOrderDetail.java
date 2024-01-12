package com.UmiUni.shop.paymentservice.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class SalesOrderDetail {

    private Long salesOrderDetailId;

    private String salesOrderSn;

    @Column(nullable = false)
    private String skuCode;  // Assuming skuCode is a unique identifier but not a foreign key

    private Long supplierId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;
}