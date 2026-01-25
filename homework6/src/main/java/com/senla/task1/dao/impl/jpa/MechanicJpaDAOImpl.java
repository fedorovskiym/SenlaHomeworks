package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.MechanicDAO;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MechanicJpaDAOImpl extends AbstractJpaDAO<Mechanic, Integer> implements MechanicDAO {

    private static final String HQL_SORT_BY = "SELECT m FROM Mechanic m ORDER BY ";

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
        } catch (Exception e) {
            transaction.rollback();
            throw e;
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
        } catch (Exception e) {
            transaction.rollback();
            throw e;
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
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
