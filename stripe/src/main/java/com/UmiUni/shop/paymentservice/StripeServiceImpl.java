package com.UmiUni.shop.paymentservice;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String apiKey;

    public StripeServiceImpl() {
        // Initialize Stripe API key
        Stripe.apiKey = this.apiKey;
    }

    @Override
    public String createCharge(ChargeRequest chargeRequest) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("source", chargeRequest.getSource()); // The source ID from Stripe.js
        chargeParams.put("description", chargeRequest.getDescription());

        try {
            Charge charge = Charge.create(chargeParams);
            return "Charge successful: " + charge.getId();
        } catch (StripeException e) {
            // Handle exception
            return "Charge failed: " + e.getMessage();
        }
    }

}
