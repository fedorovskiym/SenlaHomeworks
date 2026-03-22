package com.senla.consumer.service;

import com.senla.consumer.model.Account;
import com.senla.consumer.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findById(Long id) {
        return accountRepository.getById(id).orElse(null);
    }

    public void updateAccount(Account account) {
        accountRepository.updateAccount(account);
    }
}
