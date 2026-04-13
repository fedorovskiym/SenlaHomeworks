package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.City;
import com.senla.ProductService.repository.CityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CityRepositoryImpl extends AbstractGenericRepositoryImpl<City, Long> implements CityRepository {

    private static final String HQL_FIND_ALL_ORDER_BY_ID = """
            SELECT c FROM City c ORDER BY c.id
            """;

    private static final String HQL_FIND_BY_NAME = """
            SELECT c FROM City c WHERE c.name = :name
            """;

    public CityRepositoryImpl() {
        super(City.class);
    }

    @Override
    public List<City> findWithPagination(Integer page, Integer size) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_ORDER_BY_ID, City.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public Optional<City> findByName(String name) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.createQuery(HQL_FIND_BY_NAME, City.class)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
