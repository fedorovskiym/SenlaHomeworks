package com.senla.UserService.service.impl;

import com.senla.UserService.model.Role;
import com.senla.UserService.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    @Test
    void findByNameShouldReturnRole() {
        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        Role result = roleServiceImpl.findByName(role.getName());

        assertEquals(role, result);
    }

    @Test
    void findByNameShouldThrowEntityNotFoundException() {
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleServiceImpl.findByName(any()));
    }
}