package com.seatlock.payment.service;

import com.seatlock.payment.dto.request.PaymentRequest;
import com.seatlock.payment.dto.response.PaymentResponse;
import com.seatlock.payment.entity.Payment;
import com.seatlock.payment.exception.IdempotencyKeyReuseException;
import com.seatlock.payment.exception.PaymentInProgressException;
import com.seatlock.payment.exception.PaymentNotFoundException;
import com.seatlock.payment.provider.PaymentProvider;
import com.seatlock.payment.provider.ProviderResult;
import com.seatlock.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;

    public PaymentService(PaymentRepository paymentRepository, PaymentProvider paymentProvider) {
        this.paymentRepository = paymentRepository;
        this.paymentProvider = paymentProvider;
    }

    public PaymentResponse pay(String idempotencyKey, PaymentRequest request){
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);

        if(existing.isPresent()){
            return replay(existing.get(), request);
        }
        Payment payment;
        try {
            payment = paymentRepository.saveAndFlush(
                    Payment.pending(request.bookingId(), request.amount(), idempotencyKey));
        } catch (DataIntegrityViolationException e) {
            Payment winner = paymentRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(() -> e);
            return replay(winner, request);
        }

        ProviderResult result;
        try {
            result = paymentProvider.charge(payment.getBookingId(), payment.getAmount());
        } catch (RuntimeException e) {
            log.error("Provider error for payment {}", payment.getId(), e);
            result = ProviderResult.declined("PROVIDER_ERROR");
        }

        if (result.approved()) {
            payment.markSucceeded(result.providerRef());
        } else {
            payment.markFailed(result.failureReason());
        }
        Payment saved = paymentRepository.save(payment);

        log.info("Payment {} for booking {} -> {}", saved.getId(), saved.getBookingId(), saved.getStatus());
        return PaymentResponse.from(saved);
    }
    public PaymentResponse getById(Long id) {
        return paymentRepository.findById(id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException("Payment " + id + " not found"));
    }

    public PaymentResponse getByIdempotencyKey(String idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "No payment for Idempotency-Key '" + idempotencyKey + "'"));
    }

    private PaymentResponse replay(Payment existing, PaymentRequest request) {
        if (!existing.matches(request.bookingId(), request.amount())) {
            throw new IdempotencyKeyReuseException(existing.getIdempotencyKey());
        }
        if (existing.isPending()) {
            throw new PaymentInProgressException(existing.getIdempotencyKey());
        }
        return PaymentResponse.from(existing);
    }
}
