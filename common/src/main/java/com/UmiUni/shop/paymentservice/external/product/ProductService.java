package com.UmiUni.shop.paymentservice.external.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/api/products")
public interface ProductService {

    @PostMapping("/{skuCode}/lock")
    void lockInventory(@PathVariable("skuCode") String skuCode, @RequestParam("quantity") int quantity);

    @PostMapping("/{skuCode}/reduce")
    void reduceProductInventory(@PathVariable("skuCode") String skuCode, @RequestParam("quantity") int quantity);
}
