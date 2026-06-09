package com.senla.NotificationService.broker;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.repository.impl.NotificationRepositoryImpl;
import com.senla.NotificationService.service.LocalUserService;
import com.senla.NotificationService.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Component
public class KafkaBroker {

    public static final Logger logger = LoggerFactory.getLogger(KafkaBroker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final LocalUserService localUserService;
    private final NotificationService notificationService;

    @Autowired
    public KafkaBroker(LocalUserService localUserService, NotificationService notificationService) {
        this.localUserService = localUserService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "new-user", groupId = "notification-group"
            , containerFactory = "kafkaListenerContainerFactory")
    public void consumeNewUser(String json) {
        try {
            LocalUser localUser = objectMapper.readValue(json, LocalUser.class);
            logger.info("Recieved message from kafka with new user {}", localUser);
            localUserService.save(localUser);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }

    @KafkaListener(topics = "update-user", groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeUpdateUser(String json) {
        try {
            LocalUser localUser = objectMapper.readValue(json, LocalUser.class);
            logger.info("Recieved message from kafka with user to update {}", localUser);
            localUserService.update(localUser);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }

    //Добавь в readme информацию зачем нужны конкретные топики, чуть подробнее распиши про брокер и его надстройки
    @KafkaListener(topics = "update-product-price", groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory", batch = "true")
    public void consumeUpdatePrice(List<String> jsons) {
        jsons.forEach(json -> {
            try {
                PriceDTO priceDTO = objectMapper.readValue(json, PriceDTO.class);
                logger.info("Recieved message from kafka with price {}", priceDTO);
                notificationService.sendNotification(priceDTO);
            } catch (JsonParseException e) {
                logger.error("Error while parsing message {} from kafka", json, e);
            }
        });
    }

    @KafkaListener(topics = "delete-user", groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeDeleteUser(String json) {
        try {
            UUID id = UUID.fromString(json);
            logger.info("Recieved delete message from kafka with user id {}", id);
            localUserService.delete(id);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }
}
