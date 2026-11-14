--liquibase formatted sql

--changeset rishi:001-create-event
CREATE TABLE event (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(200) NOT NULL,
    venue        VARCHAR(200) NOT NULL,
    starts_at    DATETIME(6)  NOT NULL,
    total_seats  INT          NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT ck_event_total_seats CHECK (total_seats > 0),
    CONSTRAINT ck_event_status CHECK (status IN ('DRAFT', 'ON_SALE', 'CLOSED'))
);
--rollback DROP TABLE event;

--changeset rishi:002-create-seat
CREATE TABLE seat (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    event_id     BIGINT        NOT NULL,
    section      VARCHAR(20)   NOT NULL,
    row_label    VARCHAR(10)   NOT NULL,
    seat_number  INT           NOT NULL,
    price        DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20)   NOT NULL,
    version      BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_seat PRIMARY KEY (id),
    CONSTRAINT fk_seat_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT uk_seat_position UNIQUE (event_id, section, row_label, seat_number),
    CONSTRAINT ck_seat_status CHECK (status IN ('AVAILABLE', 'HELD', 'BOOKED')),
    CONSTRAINT ck_seat_price CHECK (price >= 0)
);
CREATE INDEX ix_seat_event_status ON seat (event_id, status);
--rollback DROP TABLE seat;
