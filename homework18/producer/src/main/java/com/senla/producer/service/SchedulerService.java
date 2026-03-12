package com.senla.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

    private final AccountService accountService;

    @Autowired
    public SchedulerService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Scheduled(initialDelay = 2000, fixedDelay = Long.MAX_VALUE)
    public void init() {
        accountService.initAccount();
    }
}
