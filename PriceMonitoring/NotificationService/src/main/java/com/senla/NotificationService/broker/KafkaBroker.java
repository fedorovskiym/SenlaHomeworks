package com.senla.NotificationService.broker;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.dto.SubscriptionDTO;
import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.service.LocalUserService;
import com.senla.NotificationService.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class KafkaBroker {

    public static final Logger logger = LoggerFactory.getLogger(KafkaBroker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final LocalUserService localUserService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public KafkaBroker(LocalUserService localUserService, SubscriptionService subscriptionService) {
        this.localUserService = localUserService;
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "new-user", groupId = "group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeNewUser(String json) {
        try {
            LocalUser localUser = objectMapper.readValue(json, LocalUser.class);
            logger.info("Recieved message from kafka with new user {}", localUser);
            localUserService.save(localUser);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }

    @KafkaListener(topics = "update-user", groupId = "group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUpdateUser(String json) {
        try {
            LocalUser localUser = objectMapper.readValue(json, LocalUser.class);
            logger.info("Recieved message from kafka with user to update {}", localUser);
            localUserService.update(localUser);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }

    @KafkaListener(topics = "subscription", groupId = "group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeSubscription(String json) {
        try {
            SubscriptionDTO subscriptionDTO = objectMapper.readValue(json, SubscriptionDTO.class);
            logger.info("Recieved message from kafka with subscription {}", subscriptionDTO);
            subscriptionService.saveSubscription(subscriptionDTO);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }

    @KafkaListener(topics = "update-product-price", groupId = "group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUpdatePrice(String json) {
        try {
            PriceDTO priceDTO = objectMapper.readValue(json, PriceDTO.class);
            logger.info("Recieved message from kafka with price {}", priceDTO);
            subscriptionService.sendMessages(priceDTO);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }
}
