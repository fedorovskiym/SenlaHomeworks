package com.senla.ProductService.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaBroker {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaBroker.class);

    @Autowired
    public KafkaBroker(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSubscriptionMessage(Long id, String json) {
        kafkaTemplate.executeInTransaction(t -> {
            t.send("subscription", String.valueOf(id), json);
            t.flush();
            logger.info("Subscription message sent to kafka {} to topic {}", json, "subscription");
            return null;
        });
    }

    public void sendUpdateProductPriceMessage(Long id, String json) {
        kafkaTemplate.executeInTransaction(t -> {
            t.send("update-product-price", String.valueOf(id), json);
            t.flush();
            logger.info("Update product price message sent to kafka {} to topic {}", json, "update_product_price");
            return null;
        });
    }
}
