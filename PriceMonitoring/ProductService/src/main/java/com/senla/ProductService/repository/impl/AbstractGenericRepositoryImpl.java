package com.senla.ProductService.repository.impl;

import com.senla.ProductService.repository.GenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public abstract class AbstractGenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {

    @PersistenceContext
    private EntityManager entityManager;
    protected Class<T> type;

    public AbstractGenericRepositoryImpl(Class<T> type) {
        this.type = type;
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }


    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        return em.createQuery("FROM " + type.getSimpleName(), type).getResultList();
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
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        return Optional.ofNullable(em.find(type, id));
    }
}
