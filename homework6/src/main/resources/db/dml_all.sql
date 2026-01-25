BEGIN;

INSERT INTO garage_place (place_number, is_empty) VALUES 
    (1, false),
    (2, false),
    (3, true);

INSERT INTO mechanic (name, surname, experience_years, is_busy) VALUES 
    ('Иван', 'Иванов', 10, true),
    ('Сергей', 'Сергеев', 5, true),
    ('Алексей', 'Алексеев', 23, false);

INSERT INTO orders (car_name, mechanic_id, garage_place_id, order_status, submission_date_time, planned_completion_date_time, completion_date_time, end_date_time, duration, price) VALUES
	('BMW M5', 1, 1, 'ACCEPTED', '2025-11-16 21:48:27.167842', NULL, '2025-11-16 21:48:27.167842', '2025-11-16 23:18:27.167842', INTERVAL '90 minutes', 10000.00),
	('Kio Rio', 2, 2, 'WAITING', '2025-11-16 21:49:19.368844', '2025-11-16 23:18:27.167842' ,NULL, '2025-11-17 00:18:27.167842', INTERVAL '60 minutes', 5500.00);

COMMIT;