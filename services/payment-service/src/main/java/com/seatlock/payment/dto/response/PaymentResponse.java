package com.seatlock.payment.dto.response;

import com.seatlock.payment.entity.Payment;
import com.seatlock.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long bookingId,
        BigDecimal amount,
        PaymentStatus status,
        String providerRef,
        String failureReason,
        Instant createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getProviderRef(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}
