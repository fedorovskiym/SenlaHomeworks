package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.City;
import com.senla.ProductService.repository.CityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CityRepositoryImpl extends AbstractGenericRepositoryImpl<City, UUID> implements CityRepository {

    private static final String HQL_FIND_BY_NAME = """
            SELECT c FROM City c WHERE c.name = :name
            """;

    private static final String HQL_FIND_ALL = """
            SELECT c FROM City c
            """;

    public CityRepositoryImpl() {
        super(City.class);
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

    @Override
    public List<City> findAllWithPagination(Integer page, Integer size) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL, City.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }
}
