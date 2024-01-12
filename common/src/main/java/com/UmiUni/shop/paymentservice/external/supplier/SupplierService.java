package com.UmiUni.shop.paymentservice.external.supplier;

import com.UmiUni.shop.paymentservice.model.Supplier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-AUTH-SERVICE")
public interface SupplierService {

    @GetMapping("/api/v1/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable("id") Long id);

}
