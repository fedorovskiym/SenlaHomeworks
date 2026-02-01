CREATE TABLE garage_place(
    id SERIAL PRIMARY KEY,
    place_number INTEGER NOT NULL,
    is_empty BOOLEAN
);

CREATE TABLE mechanic(
    id SERIAL PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    experience_years DECIMAL NOT NULL,
    is_busy BOOLEAN
);

CREATE TABLE order_status(
    id SERIAL PRIMARY KEY,
    code VARCHAR(25) NOT NULL,
    name VARCHAR(25) NOT NULL
);

CREATE TABLE orders(
    id SERIAL PRIMARY KEY,
    car_name VARCHAR(50) NOT NULL,
    mechanic_id INTEGER NOT NULL,
    garage_place_id INTEGER NOT NULL,
    order_status_id INTEGER NOT NULL,
    submission_date_time TIMESTAMP NOT NULL,
    planned_completion_date_time TIMESTAMP,
    completion_date_time TIMESTAMP,
    end_date_time TIMESTAMP,
    duration BIGINT NOT NULL,
    price DECIMAL NOT NULL,
    CONSTRAINT fk_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanic(id),
    CONSTRAINT fk_garage_place FOREIGN KEY (garage_place_id) REFERENCES garage_place(id),
    CONSTRAINT fk_order_status FOREIGN KEY (order_status_id) REFERENCES order_status(id)
);