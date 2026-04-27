package com.senla.UserService.service.impl;

import com.senla.UserService.model.User;
import com.senla.UserService.repository.UserRepository;
import com.senla.UserService.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsernameIfExists(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByPhoneNumberIfExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    @Override
    @Transactional
    public void save(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new EntityExistsException("User with username " + user.getUsername() + " already exists");
        }
        if(userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new EntityExistsException("Phone number " + user.getPhoneNumber() + " already exists");
        }
        userRepository.save(user);
    }
}
