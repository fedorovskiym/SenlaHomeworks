package com.senla.task1.repository;

import com.senla.task1.models.User;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
