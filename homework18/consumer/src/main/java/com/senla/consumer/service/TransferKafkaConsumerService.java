package com.senla.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.consumer.dto.TransferDTO;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransferKafkaConsumerService {

    private final Logger logger = LogManager.getLogger(TransferKafkaConsumerService.class);
    private final TransferService transferService;
    private final KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    public TransferKafkaConsumerService(TransferService transferService, KafkaConsumer<String, String> kafkaConsumer) {
        this.transferService = transferService;
        this.kafkaConsumer = kafkaConsumer;
    }

    @KafkaListener(topics = "transfer", groupId = "group-test",containerFactory = "kafkaListenerContainerFactory", batch = "true")
    @Transactional
    public void consumeTransfer(List<String> jsons) {
        for (String json : jsons) {
            logger.info("Получен трансфер из кафки: {}", json);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                TransferDTO transferDTO = objectMapper.readValue(json, TransferDTO.class);
                transferService.doTransfer(transferDTO);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
