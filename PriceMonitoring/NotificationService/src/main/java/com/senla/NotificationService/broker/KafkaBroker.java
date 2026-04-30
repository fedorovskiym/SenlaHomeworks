package com.senla.NotificationService.broker;

import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.service.LocalUserService;
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

    @Autowired
    public KafkaBroker(LocalUserService localUserService) {
        this.localUserService = localUserService;
    }

    @KafkaListener(topics = "new-user", groupId = "group", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
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
    @Transactional
    public void consumeUpdateUser(String json) {
        try {
            LocalUser localUser = objectMapper.readValue(json, LocalUser.class);
            logger.info("Recieved message from kafka with user to update {}", localUser);
            localUserService.update(localUser);
        } catch (JsonParseException e) {
            logger.error("Error while parsing message {} from kafka", json, e);
        }
    }
}
