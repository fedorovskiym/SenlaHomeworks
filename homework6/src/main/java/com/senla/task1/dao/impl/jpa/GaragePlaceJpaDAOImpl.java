package com.senla.task1.dao.impl.jpa;

import com.senla.task1.dao.GaragePlaceDAO;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GaragePlaceJpaDAOImpl extends AbstractJpaDAO<GaragePlace, Integer> implements GaragePlaceDAO {

    private static final String HQL_EXIST_PLACE_NUMBER = "SELECT g FROM GaragePlace g WHERE g.placeNumber = :placeNumber";
    private static final String HQL_FREE_GARAGE_PLACE = "SELECT g FROM GaragePlace g WHERE g.isEmpty = true";

    public GaragePlaceJpaDAOImpl(Class<GaragePlace> type) {
        super(type);
    }

    public GaragePlaceJpaDAOImpl() {
        super(GaragePlace.class);
    }

    @Override
    public Boolean checkIsPlaceNumberExists(int placeNumber) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            if (session.createQuery(HQL_FREE_GARAGE_PLACE).setParameter("placeNumber", placeNumber) == null) {
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
    public List<GaragePlace> findFreeGaragePlaces() {
        List<GaragePlace> freeGaragePlaces = new ArrayList<>();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            freeGaragePlaces = session.createQuery(HQL_FREE_GARAGE_PLACE).getResultList();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return freeGaragePlaces;
    }

    @Override
    public Optional<GaragePlace> findByPlaceNumber(int placeNumber) {
        GaragePlace garagePlace;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            garagePlace = session.createQuery(HQL_EXIST_PLACE_NUMBER, GaragePlace.class)
                    .setParameter("placeNumber", placeNumber)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return Optional.ofNullable(garagePlace);
    }

    @Override
    public void importWithTransaction(List<GaragePlace> garagePlaces) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            for (GaragePlace garagePlace : garagePlaces) {
                session.saveOrUpdate(garagePlace);
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
