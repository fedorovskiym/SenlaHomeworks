-- liquibase formatted sql

-- changeset fedor:1776032568370-1
ALTER TABLE shop_branches DROP COLUMN address

-- changeset fedor:1776032568370-2
ALTER TABLE shop_branches ADD COLUMN street VARCHAR(255)

-- changeset fedor:1776032568370-3
ALTER TABLE shop_branches ADD COLUMN house INTEGER

-- changeset fedor:1776032568370-4
ALTER TABLE shop_branches ADD COLUMN room INTEGER