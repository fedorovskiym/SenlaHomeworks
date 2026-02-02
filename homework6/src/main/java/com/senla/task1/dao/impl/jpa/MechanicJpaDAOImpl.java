package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.MechanicDAO;
import com.senla.task1.exceptions.JpaException;
import com.senla.task1.models.Mechanic;
import com.senla.task1.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MechanicJpaDAOImpl extends AbstractJpaDAO<Mechanic, Integer> implements MechanicDAO {

    private static final String HQL_SORT_BY = "SELECT m FROM Mechanic m ORDER BY ";
    private static final Logger logger = LogManager.getLogger(MechanicJpaDAOImpl.class);

    public MechanicJpaDAOImpl(Class<Mechanic> type) {
        super(type);
    }

    public MechanicJpaDAOImpl() {
        super(Mechanic.class);
    }

    @Override
    public List<Mechanic> sortBy(String field, boolean flag) {
        List<Mechanic> sortedList = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            String hql = HQL_SORT_BY + field + (flag ? " ASC" : " DESC");
            sortedList = session.createQuery(hql, Mechanic.class).getResultList();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при сортировке механиков по {}", field, e);
        } finally {
            session.close();
        }
        return sortedList;
    }

    @Override
    public Boolean checkIsMechanicExists(Integer id) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            if (session.get(Mechanic.class, id) == null) {
                return false;
            }
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении механика с id {}", id, e);
        } finally {
            session.close();
        }
        return true;
    }

    @Override
    public void importWithTransaction(List<Mechanic> mechanicList) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            for (Mechanic mechanic : mechanicList) {
                session.saveOrUpdate(mechanic);
            }
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при импорте", e);
        } finally {
            session.close();
        }
    }
}
