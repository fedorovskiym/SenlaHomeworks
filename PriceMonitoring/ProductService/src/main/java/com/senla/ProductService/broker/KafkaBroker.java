package com.senla.ProductService.broker;

import com.senla.ProductService.exception.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class KafkaBroker {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaBroker.class);

    @Autowired
    public KafkaBroker(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUpdateProductPriceMessage(UUID id, String json) {
        try {
            kafkaTemplate.executeInTransaction(t -> {
                t.send("update-product-price", String.valueOf(id), json);
                t.flush();
                logger.info("Update product price message sent to kafka {} to topic {}", json, "update-product-price");
                return null;
            });
        } catch (Exception e) {
            logger.error("Error while sending update product price message {} to topic {}",
                    json, "update-product-rice", e);
            throw new KafkaException("Error while update product price!");
        }
    }
}
