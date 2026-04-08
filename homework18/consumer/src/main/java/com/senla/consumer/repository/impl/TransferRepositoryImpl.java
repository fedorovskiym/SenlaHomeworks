package com.senla.consumer.repository.impl;

import com.senla.consumer.model.Transfer;
import com.senla.consumer.repository.TransferRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class TransferRepositoryImpl implements TransferRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void save(Transfer transfer) {
        EntityManager em = getEntityManager();
        em.persist(transfer);
    }
}
