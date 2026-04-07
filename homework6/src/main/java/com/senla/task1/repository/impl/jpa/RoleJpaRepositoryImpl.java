package com.senla.task1.repository.impl.jpa;

import com.senla.task1.models.Role;
import com.senla.task1.models.enums.RoleEnum;
import com.senla.task1.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleJpaRepositoryImpl extends AbstractJpaRepository<Role, Integer> implements RoleRepository {

    private static final String HQL_FIND_BY_NAME = """
            SELECT r from Role r WHERE r.name = :name
            """;

    public RoleJpaRepositoryImpl(Class<Role> type) {
        super(type);
    }

    public RoleJpaRepositoryImpl() {
        super(Role.class);
    }


    @Override
    public Optional<Role> findByName(RoleEnum name) {
        EntityManager em = getEntityManager();
        Optional<Role> role;
        role = Optional.ofNullable(em.createQuery(HQL_FIND_BY_NAME, Role.class)
                .setParameter("name", name)
                .getSingleResult());
        return role;
    }
}
