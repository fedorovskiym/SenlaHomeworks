package com.senla.consumer.repository.impl;

import com.senla.consumer.model.Account;
import com.senla.consumer.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Account getById(Long id) {
        EntityManager em = getEntityManager();
        return em.find(Account.class, id);
    }
}
