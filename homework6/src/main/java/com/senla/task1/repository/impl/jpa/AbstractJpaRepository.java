package com.senla.task1.repository.impl.jpa;

import com.senla.task1.repository.GenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJpaRepository<T, PK extends Serializable> implements GenericRepository<T, PK> {

    @PersistenceContext
    private EntityManager entityManager;
    protected Class<T> type;

    public AbstractJpaRepository(Class<T> type) {
        this.type = type;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        List<T> result;
        result = entityManager.createQuery("FROM " + type.getSimpleName(), type).getResultList();
        return result;
    }

    @Override
    public void save(T entity) {
        EntityManager em = getEntityManager();
        em.persist(entity);
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        em.remove(entity);
    }

    @Override
    public void update(T entity) {
        EntityManager em = getEntityManager();
        em.merge(entity);
    }

    @Override
    public Optional<T> findById(PK id) {
        EntityManager em = getEntityManager();
        Optional<T> entity;
        entity = Optional.ofNullable(em.find(type, id));
        return entity;
    }
}
