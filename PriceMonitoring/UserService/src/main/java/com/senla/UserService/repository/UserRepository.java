package com.senla.UserService.repository;

import com.senla.UserService.model.User;

import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Long>{

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
