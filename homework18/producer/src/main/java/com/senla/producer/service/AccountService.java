package com.senla.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.producer.model.Account;
import com.senla.producer.model.TransferMessage;
import com.senla.producer.repository.AccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AccountService {
    private final Logger logger = LogManager.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final TransferKafkaProducerService transferKafkaProducerService;
    private final Map<Long, Account> accountMap = new HashMap<>();
    private final static Integer MIN = 1000;
    private final static Integer MAX = 10000;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferKafkaProducerService transferKafkaProducerService) {
        this.accountRepository = accountRepository;
        this.transferKafkaProducerService = transferKafkaProducerService;
    }

    @Transactional
    public void initAccount() {
        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            for (int i = 0; i < 1000; i++) {
                Account account = new Account();
                account.setBalance((int) ((Math.random() * (MAX - MIN)) + MIN));
                accounts.add(account);
            }
            accountRepository.save(accounts);
            accounts.forEach(account -> accountMap.put(account.getId(), account));
        } else {
            accounts.forEach(account -> accountMap.put(account.getId(), account));
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void generateTransfer() throws JsonProcessingException {
        if (accountMap.isEmpty()) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            Long fromAccountId = randomId();
            Long targetAccountId = randomId();
            Integer amount = (int) (Math.random() * (MAX - MIN)) + MIN;
            TransferMessage transfer = new TransferMessage(fromAccountId, targetAccountId, amount);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(transfer);
            logger.info("Создался перевод {}", json);
            transferKafkaProducerService.sendTransferMessageToKafka(transfer.getId(), json);
        }
    }

    private Long randomId() {
        List<Long> keysAsArray = new ArrayList<>(accountMap.keySet());
        Random random = new Random();
        return keysAsArray.get(random.nextInt(keysAsArray.size()));
    }

}
