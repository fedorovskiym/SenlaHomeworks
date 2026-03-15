package com.senla.producer.service;

import com.senla.producer.model.TransferMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransferKafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = LogManager.getLogger(TransferKafkaProducerService.class);

    @Autowired
    public TransferKafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendTransferMessageToKafka(Long fromId, String json) {
        kafkaTemplate.send("transfer", String.valueOf(fromId), json);
        logger.info("Сообщение отправлено в кафку: {}", json);
    }
}
