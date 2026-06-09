package com.senla.UserService.repository.impl;

import com.senla.UserService.model.User;
import com.senla.UserService.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl extends AbstractGenericRepositoryImpl<User, UUID> implements UserRepository {

    private static final String HQL_FIND_BY_USERNAME = """
            SElECT u FROM User u
            JOIN FETCH u.role
            WHERE u.username = :username
            """;

    private static final String HQL_FIND_BY_PHONE_NUMBER = """
            SElECT u FROM User u
            WHERE u.phoneNumber = :phoneNumber
            """;

    public UserRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_USERNAME, User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_PHONE_NUMBER, User.class)
                .setParameter("phoneNumber", phoneNumber)
                .getResultStream()
                .findFirst();
    }
}
