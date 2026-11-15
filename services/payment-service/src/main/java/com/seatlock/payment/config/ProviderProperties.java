package com.seatlock.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "seatlock.provider")
public record ProviderProperties(
        double failureRate,
        double timeoutRate,
        Duration minLatency,
        Duration maxLatency,
        Duration timeoutLatency
) {
    public ProviderProperties{
        requireRate("failure-rate",failureRate);
        requireRate("timeout-rate",timeoutRate);
        if(minLatency.compareTo(maxLatency) > 0){
            throw new IllegalArgumentException("min-Latency must not exceed max-Latency");
        }
    }

    private static void requireRate(String name, double value){
        if(value < 0.0 || value > 1.0){
            throw  new IllegalArgumentException(name + "must be between 0.0 and 1.0, was " + value);
        }
    }
}
