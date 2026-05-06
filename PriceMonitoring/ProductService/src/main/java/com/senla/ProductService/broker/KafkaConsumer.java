package com.senla.ProductService.broker;

import com.senla.ProductService.service.SubscriptionService;
import jdk.security.jarsigner.JarSignerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final SubscriptionService subscriptionService;

    public KafkaConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "delete-user", groupId = "product-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeDeleteSubscriptionMessage(String json) {
        try {
            UUID id = UUID.fromString(json);
            logger.info("Received delete subsccrion message from kafka by user id {}", id);
            subscriptionService.deleteByUserId(id);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }
}
