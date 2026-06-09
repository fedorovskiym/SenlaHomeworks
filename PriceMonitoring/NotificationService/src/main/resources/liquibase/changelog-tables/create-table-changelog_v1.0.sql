-- liquibase formatted sql

-- changeset fedor:1777552447993-1
CREATE TABLE local_users
(
    id           UUID NOT NULL,
    phone_number VARCHAR(255),
    CONSTRAINT pk_local_users PRIMARY KEY (id)
);

-- changeset fedor:1777552447993-2
CREATE TABLE notifications
(
    id             UUID NOT NULL,
    message        VARCHAR(255),
    send_date_time TIMESTAMP WITHOUT TIME ZONE,
    user_id        UUID,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

-- changeset fedor:1777552447993-3
ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES local_users (id);




