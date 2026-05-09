package com.senla.UserService.broker;

import com.senla.UserService.exception.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void sendMessageWithNewUser(UUID id, String json) {
        try {
            kafkaTemplate.executeInTransaction(t -> {
                t.send("new-user", String.valueOf(id), json);
                t.flush();
                logger.info("User with id {} and data {} send to topic {}", id, json, "new-user");
                return null;
            });
        } catch (Exception e) {
            logger.error("Error while sending create user message with id {} and data {}", id, json, e);
            throw new KafkaException("Internal server error while registration!");
        }
    }

    @Transactional
    public void sendMessageWithUpdateUser(UUID id, String json) {
        try {
            kafkaTemplate.executeInTransaction(t -> {
                t.send("update-user", String.valueOf(id), json);
                t.flush();
                logger.info("Update message user with id {} and data {} send to topic {}", id, json, "update-user");
                return null;
            });
        } catch (Exception e) {
            logger.error("Error while sending update user message with id {} and data {}", id, json, e);
            throw new KafkaException("Internal server error while update data!");
        }
    }

    @Transactional
    public void sendMessageWithDeleteUser(UUID id, String json) {
        try {
            kafkaTemplate.executeInTransaction(t -> {
                t.send("delete-user", String.valueOf(id), json);
                t.flush();
                logger.info("Delete message with user id {} send to topic {}", id, "delete-user");
                return null;
            });
        } catch (Exception e) {
            logger.error("Error while sending delete user message with id {} and data {}", id, json, e);
            throw new KafkaException("Internal server error while delete user data!");
        }
    }
}
