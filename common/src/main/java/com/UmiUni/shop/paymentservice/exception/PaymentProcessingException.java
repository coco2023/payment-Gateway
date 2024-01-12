package com.UmiUni.shop.paymentservice.exception;


import com.UmiUni.shop.paymentservice.constant.ErrorCategory;

public class PaymentProcessingException extends RuntimeException {

    private ErrorCategory category;

    public PaymentProcessingException(String message) {
        super(message);
    }

    // Constructor
    public PaymentProcessingException(String message, ErrorCategory category) {
        super(message);
        this.category = category;
    }

    // Getter
    public ErrorCategory getCategory() {
        return category;
    }

}