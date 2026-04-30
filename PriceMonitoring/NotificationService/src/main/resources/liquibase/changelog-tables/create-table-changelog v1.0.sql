-- liquibase formatted sql

-- changeset fedor:1777552447993-1
CREATE TABLE local_users
(
    id           BIGINT NOT NULL,
    phone_number VARCHAR(255),
    CONSTRAINT pk_local_users PRIMARY KEY (id)
);

