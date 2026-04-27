package com.senla.UserService.service;

import com.senla.UserService.model.User;

public interface UserService {

    User findByUsername(String username);

    User findByUsernameIfExists(String username);

    User findByPhoneNumberIfExists(String phoneNumber);

    void save(User user);
}
