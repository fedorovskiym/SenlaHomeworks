-- liquibase formatted sql

-- changeset fedor:1777048897650-1
CREATE TABLE roles
(
    id   UUID NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

-- changeset fedor:1777048897650-2
CREATE TABLE users
(
    id                UUID NOT NULL,
    username          VARCHAR(255),
    phone_number      VARCHAR(255),
    password          VARCHAR(255),
    role_id           UUID,
    registration_date date,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset fedor:1777048897650-3
ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

-- changeset fedor:1777048897650-4
INSERT INTO roles(id, name)
VALUES (gen_random_uuid(), 'ROLE_ADMIN'),
       (gen_random_uuid(), 'ROLE_USER')


