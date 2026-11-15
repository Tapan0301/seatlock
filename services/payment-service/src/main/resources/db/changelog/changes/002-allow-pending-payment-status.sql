--liquibase formatted sql

--changeset rishi:002-allow-pending-payment-status
ALTER TABLE payment DROP CHECK ck_payment_status;
ALTER TABLE payment ADD CONSTRAINT ck_payment_status CHECK (status IN ('PENDING', 'SUCCEEDED', 'FAILED'));
--rollback ALTER TABLE payment DROP CHECK ck_payment_status;
--rollback ALTER TABLE payment ADD CONSTRAINT ck_payment_status CHECK (status IN ('SUCCEEDED', 'FAILED'));