--liquibase formatted sql

--changeset rishi:003-create-booking
CREATE TABLE booking (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    event_id         BIGINT        NOT NULL,
    seat_id          BIGINT        NOT NULL,
    user_id          VARCHAR(64)   NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    amount           DECIMAL(10,2) NOT NULL,
    idempotency_key  VARCHAR(64)   NOT NULL,
    request_hash     VARCHAR(64)   NOT NULL,
    hold_expires_at  DATETIME(6)   NOT NULL,
    payment_ref      VARCHAR(64)   NULL,
    failure_reason   VARCHAR(255)  NULL,
    version          BIGINT        NOT NULL DEFAULT 0,
    created_at       DATETIME(6)   NOT NULL,
    updated_at       DATETIME(6)   NOT NULL,
    -- equals seat_id while the booking is active (PENDING/CONFIRMED), NULL otherwise
    active_seat_id   BIGINT GENERATED ALWAYS AS
                     (CASE WHEN status IN ('PENDING', 'CONFIRMED') THEN seat_id END) STORED,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_booking_seat  FOREIGN KEY (seat_id)  REFERENCES seat (id),
    CONSTRAINT uk_booking_idempotency UNIQUE (user_id, idempotency_key),
    CONSTRAINT uk_booking_active_seat UNIQUE (active_seat_id),
    CONSTRAINT ck_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'FAILED', 'RELEASED'))
);
CREATE INDEX ix_booking_user ON booking (user_id, created_at);
CREATE INDEX ix_booking_pending_expiry ON booking (status, hold_expires_at);
--rollback DROP TABLE booking;
