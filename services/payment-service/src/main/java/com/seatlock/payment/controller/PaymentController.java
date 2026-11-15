package com.seatlock.payment.controller;

import com.seatlock.payment.dto.request.PaymentRequest;
import com.seatlock.payment.dto.response.PaymentResponse;
import com.seatlock.payment.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse pay(@RequestHeader("Idempotency-Key") @NotBlank @Size(max = 64)
                                   String idempotencyKey,
                               @Valid @RequestBody PaymentRequest request
                               )
    {
        return paymentService.pay(idempotencyKey, request);
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @GetMapping(params = "idempotencyKey")
    public PaymentResponse getByIdempotencyKey(@RequestParam String idempotencyKey) {
        return paymentService.getByIdempotencyKey(idempotencyKey);
    }
}
