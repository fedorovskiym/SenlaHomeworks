package com.senla.consumer.repository;

import com.senla.consumer.model.Account;

public interface AccountRepository {

    Account getById(Long id);
}
