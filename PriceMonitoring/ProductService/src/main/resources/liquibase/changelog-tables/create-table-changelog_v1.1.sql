-- liquibase formatted sql

-- changeset fedor:1776032568369-1
CREATE TABLE city
(
    id   UUID NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_city PRIMARY KEY (id)
);

-- changeset fedor:1776032568369-2
ALTER TABLE shop_branches DROP COLUMN city

-- changeset fedor:1776032568369-3
ALTER TABLE shop_branches ADD COLUMN city_id UUID

-- changeset fedor:1776032568369-4
ALTER TABLE shop_branches
    ADD CONSTRAINT FK_SHOP_BRANCHES_ON_CITY FOREIGN KEY (city_id) REFERENCES city (id) ON DELETE RESTRICT ;

