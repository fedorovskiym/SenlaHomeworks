package com.senla.UserService.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        kafkaTemplate.executeInTransaction(t -> {
            t.send("new-user", String.valueOf(id), json);
            t.flush();
            logger.info("User with id {} and data {} send to topic {}", id, json, "new-user");
            return null;
        });
    }

    @Transactional
    public void sendMessageWithUpdateUser(UUID id, String json) {
        kafkaTemplate.executeInTransaction(t -> {
            t.send("update-user", String.valueOf(id), json);
            t.flush();
            logger.info("Update user with id {} and data {} send to topic {}", id, json, "update-user");
            return null;
        });
    }
}
