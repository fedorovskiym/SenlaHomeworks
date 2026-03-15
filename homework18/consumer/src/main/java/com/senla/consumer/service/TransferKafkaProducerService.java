package com.senla.consumer.service;

import com.senla.consumer.model.Transfer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferKafkaProducerService {

    private final Logger logger = LogManager.getLogger(TransferKafkaProducerService.class);

    @KafkaListener(topics = "transfer", groupId = "group-test")
    @Transactional
    public void consumeTransfer(String json) {
        logger.info("Получен трансфер из кафки: {}", json);
    }

}
