--liquibase formatted sql

--changeset rishi:001-create-payment
CREATE TABLE payment (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    booking_id       BIGINT        NOT NULL,
    amount           DECIMAL(10,2) NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    provider_ref     VARCHAR(64)   NULL,
    failure_reason   VARCHAR(255)  NULL,
    idempotency_key  VARCHAR(64)   NOT NULL,
    created_at       DATETIME(6)   NOT NULL,
    updated_at       DATETIME(6)   NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id),
    CONSTRAINT uk_payment_idempotency UNIQUE (idempotency_key),
    CONSTRAINT ck_payment_status CHECK (status IN ('SUCCEEDED', 'FAILED')),
    CONSTRAINT ck_payment_amount CHECK (amount > 0)
);
CREATE INDEX ix_payment_booking ON payment (booking_id);
--rollback DROP TABLE payment;
