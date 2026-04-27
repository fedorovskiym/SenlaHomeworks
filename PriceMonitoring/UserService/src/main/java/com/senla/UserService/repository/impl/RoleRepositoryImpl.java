package com.senla.UserService.repository.impl;

import com.senla.UserService.model.Role;
import com.senla.UserService.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleRepositoryImpl extends AbstractGenericRepositoryImpl<Role, Long> implements RoleRepository {

    private static final String HQL_FIND_ROLE_BY_NAME = """
            SELECT r FROM Role r WHERE r.name = :name
            """;

    public RoleRepositoryImpl() {
        super(Role.class);
    }

    @Override
    public Optional<Role> findByName(String name) {
        EntityManager entityManager = getEntityManager();
        return Optional.ofNullable(entityManager.createQuery(HQL_FIND_ROLE_BY_NAME, Role.class)
                .setParameter("name", name)
                .getSingleResult());
    }

}
