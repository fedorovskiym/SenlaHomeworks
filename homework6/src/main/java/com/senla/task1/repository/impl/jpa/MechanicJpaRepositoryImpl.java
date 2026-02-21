package com.senla.task1.repository.impl.jpa;

import com.senla.task1.models.Mechanic;
import com.senla.task1.repository.MechanicRepository;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("mechanicJpaDAO")
public class MechanicJpaRepositoryImpl extends AbstractJpaRepository<Mechanic, Integer> implements MechanicRepository {

    private static final String HQL_SORT_BY = "SELECT m FROM Mechanic m ORDER BY ";
    private static final Logger logger = LogManager.getLogger(MechanicJpaRepositoryImpl.class);

    public MechanicJpaRepositoryImpl(Class<Mechanic> type) {
        super(type);
    }

    public MechanicJpaRepositoryImpl() {
        super(Mechanic.class);
    }

    @Override
    public List<Mechanic> sortBy(String field, boolean flag) {
        EntityManager em = getEntityManager();
        List<Mechanic> sortedList;
        String hql = HQL_SORT_BY + field + (flag ? " ASC" : " DESC");
        sortedList = em.createQuery(hql, Mechanic.class).getResultList();
        return sortedList;
    }

    @Override
    public Boolean checkIsMechanicExists(Integer id) {
        EntityManager em = getEntityManager();
        if (em.find(Mechanic.class, id) == null) {
            return false;
        }
        return true;
    }
}
