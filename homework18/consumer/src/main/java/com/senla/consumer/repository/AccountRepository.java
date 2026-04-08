package com.senla.consumer.repository;

import com.senla.consumer.model.Account;

import java.util.Optional;

public interface AccountRepository {

    Optional<Account> getById(Long id);
    void updateAccount(Account account);
}
