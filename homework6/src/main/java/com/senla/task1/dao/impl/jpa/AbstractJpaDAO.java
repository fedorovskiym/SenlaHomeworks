package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.GenericDAO;
import com.senla.task1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJpaDAO<T, PK extends Serializable> implements GenericDAO<T, PK> {

    private Class<T> type;

    public AbstractJpaDAO(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> findAll() {
        List<T> result;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            result = session.createQuery("FROM " + type.getSimpleName(), type).getResultList();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
        return result;
    }

    @Override
    public void save(T entity) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void delete(T entity) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void update(T entity) {

    }

    @Override
    public Optional<T> findBy(PK id) {
        T entity;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            entity = (T) session.createQuery("FROM " + type.getSimpleName() + " u WHERE u.id = :id", type).setParameter("id", id);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
        return Optional.ofNullable(entity);
    }
}
