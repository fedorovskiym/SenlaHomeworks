CREATE TABLE subscriptions
(
    id               UUID NOT NULL,
    product_price_id UUID,
    user_id          UUID,
    CONSTRAINT pk_subscriptions PRIMARY KEY (id)
);

ALTER TABLE subscriptions
    ADD CONSTRAINT FK_SUBSCRIPTIONS_ON_PRODUCT_PRICE FOREIGN KEY (product_price_id) REFERENCES product_prices (id) ON DELETE CASCADE;


