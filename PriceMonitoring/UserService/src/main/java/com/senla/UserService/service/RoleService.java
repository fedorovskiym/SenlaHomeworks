package com.senla.UserService.service;

import com.senla.UserService.model.Role;
import jakarta.persistence.EntityNotFoundException;

/**
 * interface for working with roles
 */
public interface RoleService {

    /**
     * method for finding role by name
     *
     * @param name contains role name
     * @return role with name from request
     * @throws EntityNotFoundException if role with name from request not found
     */
    Role findByName(String name);
}
