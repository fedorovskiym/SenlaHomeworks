package com.senla.task1.repository.impl.jpa;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.repository.GaragePlaceRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("garagePlaceJpaDAO")
public class GaragePlaceJpaRepositoryImpl extends AbstractJpaRepository<GaragePlace, Integer> implements GaragePlaceRepository {

    private static final String HQL_EXIST_PLACE_NUMBER = """
            SELECT g FROM GaragePlace g WHERE g.placeNumber = :placeNumber
            """;
    private static final String HQL_FREE_GARAGE_PLACE = """
            SELECT g FROM GaragePlace g WHERE g.isEmpty = true
            """;

    public GaragePlaceJpaRepositoryImpl(Class<GaragePlace> type) {
        super(type);
    }

    public GaragePlaceJpaRepositoryImpl() {
        super(GaragePlace.class);
    }

    @Override
    public Boolean checkIsPlaceNumberExists(Integer placeNumber) {
        EntityManager em = getEntityManager();
        if (em.createQuery(HQL_EXIST_PLACE_NUMBER).setParameter("placeNumber", placeNumber) == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<GaragePlace> findFreeGaragePlaces() {
        EntityManager em = getEntityManager();
        return em.createQuery(HQL_FREE_GARAGE_PLACE).getResultList();
    }

    @Override
    public Optional<GaragePlace> findByPlaceNumber(int placeNumber) {
        EntityManager em = getEntityManager();
        Optional<GaragePlace> garagePlace;
        garagePlace = Optional.ofNullable(em.createQuery(HQL_EXIST_PLACE_NUMBER, GaragePlace.class)
                .setParameter("placeNumber", placeNumber)
                .getSingleResult());
        return garagePlace;
    }
}
