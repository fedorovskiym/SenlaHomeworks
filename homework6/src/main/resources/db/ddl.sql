CREATE TABLE garage_place (
    id SERIAL PRIMARY KEY,
    place_number INTEGER NOT NULL,
    is_empty BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE mechanic (
    id SERIAL PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    experience_years DECIMAL NOT NULL,
    is_busy BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE order_status (
    id SERIAL PRIMARY KEY,
    code VARCHAR(25) NOT NULL,
    name VARCHAR(25) NOT NULL
);

CREATE TABLE orders (
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
COMMENT ON TABLE garage_place IS 'Таблица для хранения данных о местах в гараже';
COMMENT ON TABLE mechanic IS 'Таблица для хранения данных о механиках';
COMMENT ON TABLE order_status IS 'Таблица для хранения данных о статусе заказа';
COMMENT ON TABLE orders IS 'Таблица для хранения данных о заказах';

COMMENT ON COLUMN garage_place.id IS 'Уникальный id для мест в гараже';
COMMENT ON COLUMN garage_place.place_number IS 'Номер места в гараже';
COMMENT ON COLUMN garage_place.is_empty IS 'Статус места, занято или свободно (true, false)';

COMMENT ON COLUMN mechanic.id IS 'Уникальный id для механиков';
COMMENT ON COLUMN mechanic.name IS 'Имя механика';
COMMENT ON COLUMN mechanic.surname IS 'Фамилия механика';
COMMENT ON COLUMN mechanic.experience_years IS 'Опыт механика в годах';
COMMENT ON COLUMN mechanic.is_busy IS 'Занят ли механик заказом (true, false)';

COMMENT ON COLUMN order_status.id IS 'Уникальный id для статуса заказа';
COMMENT ON COLUMN order_status.code IS 'Код для статуса заказа (WAITING, ACCEPTED, т.д.)';
COMMENT ON COLUMN order_status.name IS 'Название для отображения';

COMMENT ON COLUMN orders.id IS 'Уникальный id для заказов';
COMMENT ON COLUMN orders.car_name IS 'Название машины на обслуживание';
COMMENT ON COLUMN orders.mechanic_id IS 'Id механика, который поставлен на данный заказ';
COMMENT ON COLUMN orders.garage_place_id IS 'Id места в гараже для этого заказа';
COMMENT ON COLUMN orders.order_status_id IS 'Id статуса заказа';
COMMENT ON COLUMN orders.submission_date_time IS 'Дата и время подачи заявки на обслуживание';
COMMENT ON COLUMN orders.planned_completion_date_time IS 'Планируемая дата и время завершения';
COMMENT ON COLUMN orders.completion_date_time IS 'Дата и время завершения';
COMMENT ON COLUMN orders.end_date_time IS 'Предполагаемое время окончания';
COMMENT ON COLUMN orders.duration IS 'Длительность заказа (в секундах)';
COMMENT ON COLUMN orders.price IS 'Цена';
