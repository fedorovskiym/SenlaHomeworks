package com.senla.task1.repository;

import com.senla.task1.models.Role;
import com.senla.task1.models.enums.RoleEnum;

import java.util.Optional;

public interface RoleRepository extends GenericRepository<Role, Integer> {

    Optional<Role> findByName(RoleEnum name);
}
