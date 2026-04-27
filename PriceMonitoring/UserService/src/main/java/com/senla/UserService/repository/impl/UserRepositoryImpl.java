package com.senla.UserService.repository.impl;

import com.senla.UserService.model.User;
import com.senla.UserService.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl extends AbstractGenericRepositoryImpl<User, Long> implements UserRepository {

    private static final String HQL_FIND_BY_USERNAME = """
            SElECT u FROM User u
            JOIN FETCH u.role
            WHERE u.username = :username
            """;

    public UserRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        EntityManager entityManager = getEntityManager();

        Optional<User> user =  entityManager.createQuery(HQL_FIND_BY_USERNAME, User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();

        return user;
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }
}
