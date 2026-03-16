package com.senla.task1.repository.impl.jpa;

import com.senla.task1.models.User;
import com.senla.task1.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserJpaRepositoryImpl extends AbstractJpaRepository<User, Integer> implements UserRepository {


    private static final String HQL_FIND_BY_LOGIN = """
            SELECT u FROM User u WHERE u.username = :username
            """;

    public UserJpaRepositoryImpl(Class<User> type) {
        super(type);
    }

    public UserJpaRepositoryImpl() {
        super(User.class);
    }


    @Override
    public Optional<User> findByUsername(String username) {
        EntityManager em = getEntityManager();
        Optional<User> user = em.createQuery(HQL_FIND_BY_LOGIN, User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
        return user;
    }
}
