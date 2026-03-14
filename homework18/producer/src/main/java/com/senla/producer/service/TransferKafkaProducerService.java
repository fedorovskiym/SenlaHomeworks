package com.senla.producer.service;

import com.senla.producer.model.TransferMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransferKafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LogManager.getLogger(TransferKafkaProducerService.class);

    @Autowired
    public TransferKafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendTransferMessageToKafka(TransferMessage transferMessage) {
        kafkaTemplate.send("transfer", transferMessage);
        logger.info("Сообщение отправлено в кафку: {}", transferMessage);
    }
}
