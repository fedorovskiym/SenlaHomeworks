package com.senla.consumer.repository.impl;

import com.senla.consumer.model.Account;
import com.senla.consumer.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<Account> getById(Long id) {
        EntityManager em = getEntityManager();
        return Optional.ofNullable(em.find(Account.class, id));
    }

    @Override
    public void updateAccount(Account account) {
        EntityManager em = getEntityManager();
        em.merge(account);
    }
}
