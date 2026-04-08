package com.senla.producer.repository;

import com.senla.producer.model.Account;

import java.util.List;

public interface AccountRepository {
    List<Account> findAll();
    void save(List<Account> account);
}
