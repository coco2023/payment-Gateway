package com.UmiUni.shop.paymentservice.external.salesOrder;

import com.UmiUni.shop.paymentservice.model.SalesOrderDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "SALES-ORDER-SERVICE/api/v1/salesOrderDetails")
public interface SalesOrderDetailService {

    // salesOrderDetail
    @GetMapping("/salesOrderSn/{salesOrderSn}")
    public ResponseEntity<List<SalesOrderDetail>> getSalesOrderDetailsBySalesOrderSn(@PathVariable("salesOrderSn") String salesOrderSn);

}
