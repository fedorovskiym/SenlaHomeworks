package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.GenericDAO;
import com.senla.task1.exceptions.JpaException;
import com.senla.task1.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJpaDAO<T, PK extends Serializable> implements GenericDAO<T, PK> {

    protected Class<T> type;
    private static final Logger logger = LogManager.getLogger(AbstractJpaDAO.class);

    public AbstractJpaDAO(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            result = session.createQuery("FROM " + type.getSimpleName(), type).getResultList();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении сущностей класса {}", type.getSimpleName(), e);
        } finally {
            session.close();
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
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при сохранении сущности {}", type.getSimpleName(), e);
        } finally {
            session.close();
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
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при удалении сущности класса {}", type.getSimpleName(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public void update(T entity) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при обновлении сущности класса {}", type.getSimpleName(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<T> findById(PK id) {
        T entity = null;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            entity = session.get(type, id);
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении по id сущности {}", type.getSimpleName(), e);
        } finally {
            session.close();
        }
        return Optional.ofNullable(entity);
    }
}
