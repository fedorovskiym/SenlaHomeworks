package com.senla.UserService.repository;

import com.senla.UserService.model.Role;

import java.util.Optional;

public interface RoleRepository extends GenericRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
