package com.senla.UserService.service;

import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.model.User;

public interface UserService {

    User findByUsername(String username);

    User findByUsernameIfExists(String username);

    User findByPhoneNumberIfExists(String phoneNumber);

    void save(User user);

    Long getPrincipalId();

    UserDTO getUserById(Long id);

    void update(Long userId, UserDTO userDTO);
}
