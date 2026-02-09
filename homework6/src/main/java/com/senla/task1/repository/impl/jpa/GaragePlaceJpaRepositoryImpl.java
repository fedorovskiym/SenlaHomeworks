package com.senla.task1.repository.impl.jpa;

import com.senla.task1.exceptions.JpaException;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.repository.GaragePlaceRepository;
import com.senla.task1.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("garagePlaceJpaDAO")
public class GaragePlaceJpaRepositoryImpl extends AbstractJpaRepository<GaragePlace, Integer> implements GaragePlaceRepository {

    private static final String HQL_EXIST_PLACE_NUMBER = "SELECT g FROM GaragePlace g WHERE g.placeNumber = :placeNumber";
    private static final String HQL_FREE_GARAGE_PLACE = "SELECT g FROM GaragePlace g WHERE g.isEmpty = true";
    private static final Logger logger = LogManager.getLogger(GaragePlaceJpaRepositoryImpl.class);

    public GaragePlaceJpaRepositoryImpl(Class<GaragePlace> type) {
        super(type);
    }

    public GaragePlaceJpaRepositoryImpl() {
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
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при проверке свободно ли место {}", placeNumber, e);
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
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении свободных мест", e);
        } finally {
            session.close();
        }
        return freeGaragePlaces;
    }

    @Override
    public Optional<GaragePlace> findByPlaceNumber(int placeNumber) {
        GaragePlace garagePlace = null;
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction = session.beginTransaction();
            garagePlace = session.createQuery(HQL_EXIST_PLACE_NUMBER, GaragePlace.class)
                    .setParameter("placeNumber", placeNumber)
                    .uniqueResult();
            transaction.commit();
        } catch (JpaException e) {
            transaction.rollback();
            logger.error("Ошибка при нахождении места по его номеру {}", placeNumber, e);
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
        } catch (JpaException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
