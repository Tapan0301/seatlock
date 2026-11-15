package com.seatlock.payment.entity;

import com.seatlock.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long bookingId;

    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    private PaymentStatus status;

    private String providerRef;

    private String failureReason;

    @Column(nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected  Payment() {
    }

    private Payment(Long bookingId, BigDecimal amount, String idempotencyKey){
        this.bookingId = bookingId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.status = PaymentStatus.PENDING;
    }

    public static Payment pending(Long bookingId, BigDecimal amount, String idempotencyKey){
        return new Payment(bookingId, amount, idempotencyKey);
    }

    public void markSucceeded(String providerRef){
        requirePending();
        this.status = PaymentStatus.SUCCEEDED;
        this.providerRef = providerRef;
    }

    public void markFailed(String failureReason){
        requirePending();
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    public boolean isPending(){
        return status == PaymentStatus.PENDING;
    }

    public boolean matches(Long bookingId, BigDecimal amount){
        return this.bookingId.equals(bookingId) && this.amount.compareTo(amount) == 0;
    }

    private void requirePending(){
        if(status != PaymentStatus.PENDING) {
          throw new IllegalStateException("Payment" + id + "is already" + status);
        }
    }

    @PrePersist
    void onCreate(){
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
