--liquibase formatted sql

--changeset rishi:004-seed-demo-event context:dev
INSERT INTO event (id, name, venue, starts_at, total_seats, status, created_at)
VALUES (1, 'SeatLock Demo Concert', 'Demo Arena', '2027-01-31 19:30:00', 50, 'ON_SALE', NOW(6));

INSERT INTO seat (event_id, section, row_label, seat_number, price, status, version)
WITH RECURSIVE n AS (
    SELECT 1 AS i
    UNION ALL
    SELECT i + 1 FROM n WHERE i < 50
)
SELECT 1, 'A', CHAR(64 + CEIL(i / 10) USING utf8mb4), ((i - 1) MOD 10) + 1, 1500.00, 'AVAILABLE', 0
FROM n;
--rollback DELETE FROM seat WHERE event_id = 1; DELETE FROM event WHERE id = 1;
