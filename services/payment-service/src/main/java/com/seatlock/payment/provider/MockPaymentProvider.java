package com.seatlock.payment.provider;

import com.seatlock.payment.config.ProviderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockPaymentProvider implements PaymentProvider{
    private static final Logger log = LoggerFactory.getLogger(MockPaymentProvider.class);

    private final ProviderProperties properties;

    public MockPaymentProvider(ProviderProperties properties) {
        this.properties = properties;
    }

    @Override
    public ProviderResult charge(Long bookingId, BigDecimal amount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (random.nextDouble() < properties.timeoutRate()) {
            log.warn("Simulating a slow provider ({}) for booking {}",
                    properties.timeoutLatency(), bookingId);
            sleep(properties.timeoutLatency());
        } else {
            sleep(randomLatency(random));
        }

        if (random.nextDouble() < properties.failureRate()) {
            return ProviderResult.declined("CARD_DECLINED");
        }
        String reference = "mock_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        return ProviderResult.approved(reference);
    }

    private Duration randomLatency(ThreadLocalRandom random) {
        long min = properties.minLatency().toMillis();
        long max = properties.maxLatency().toMillis();
        return Duration.ofMillis(min == max ? min : random.nextLong(min, max + 1));
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Payment provider call was interrupted", e);
        }
    }
}
