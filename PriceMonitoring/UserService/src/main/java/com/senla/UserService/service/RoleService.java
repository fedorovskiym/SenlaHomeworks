package com.senla.UserService.service;

import com.senla.UserService.model.Role;

public interface RoleService {

    Role findByName(String name);
}
