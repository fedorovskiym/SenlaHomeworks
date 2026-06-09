package com.senla.UserService.service.impl;

import com.senla.UserService.model.Role;
import com.senla.UserService.repository.RoleRepository;
import com.senla.UserService.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        logger.info("Finding role by name {}", name);
        return roleRepository.findByName(name).orElseThrow(() -> {
            logger.warn("Role with name {} not found", name);
            return new EntityNotFoundException("Role with name " + name + " not found");
        });
    }
}
