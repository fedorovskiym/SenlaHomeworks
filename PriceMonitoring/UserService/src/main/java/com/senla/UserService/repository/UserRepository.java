package com.senla.UserService.repository;

import com.senla.UserService.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends GenericRepository<User, UUID>{

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
