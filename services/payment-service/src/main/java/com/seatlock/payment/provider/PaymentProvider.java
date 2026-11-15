package com.seatlock.payment.provider;

import java.math.BigDecimal;

public interface PaymentProvider {
    ProviderResult charge(Long bookingId, BigDecimal amount);
}
