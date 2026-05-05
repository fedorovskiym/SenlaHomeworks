package com.senla.UserService.repository;

import com.senla.UserService.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends GenericRepository<Role, UUID> {

    Optional<Role> findByName(String name);
}
