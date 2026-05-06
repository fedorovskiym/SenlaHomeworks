package com.senla.UserService.service;

import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User findByUsername(String username);

    User findByUsernameIfExists(String username);

    User findByPhoneNumberIfExists(String phoneNumber);

    void save(User user);

    UUID getPrincipalId();

    UserDTO getUserById(UUID id);

    void update(UUID userId, UserDTO userDTO);

    List<UserDTO> findAll();

    void delete(UUID id);

    User findByIdIfExists(UUID id);
}
