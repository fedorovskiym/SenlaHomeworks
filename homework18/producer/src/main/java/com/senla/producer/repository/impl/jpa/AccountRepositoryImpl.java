package com.senla.producer.repository.impl.jpa;

import com.senla.producer.model.Account;
import com.senla.producer.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Account> findAll() {
        EntityManager em = getEntityManager();
        List<Account> result = em.createQuery("FROM Account").getResultList();
        return result;
    }

    @Override
    public void save(List<Account> accountList) {
        EntityManager em = getEntityManager();
        for(Account account : accountList) {
            em.persist(account);
        }
    }
}
