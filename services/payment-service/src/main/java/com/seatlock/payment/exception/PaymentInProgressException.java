package com.seatlock.payment.exception;

public class PaymentInProgressException extends RuntimeException{
    public PaymentInProgressException(String idempotencyKey){
        super("Payment for Idempotency-Key '" +
                idempotencyKey + "' is still being processed; retry shortly");
    }
}
