package com.senla.UserService.service.impl;

import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.mapper.UserMapper;
import com.senla.UserService.model.User;
import com.senla.UserService.repository.UserRepository;
import com.senla.UserService.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
        if (findByUsernameIfExists(user.getUsername()) != null) {
            throw new EntityExistsException("User with username " + user.getUsername() + " already exists");
        }
        if (findByPhoneNumberIfExists(user.getPhoneNumber()) != null) {
            throw new EntityExistsException("Phone number " + user.getPhoneNumber() + " already exists");
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public Long getPrincipalId() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findByUsername(principal.getUsername()).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userMapper.userToUserDTO(userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id - " + id + " not found!")));
    }

    @Override
    @Transactional
    public void update(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id - " + userId + " not found!"));

        user = userMapper.updateUserFromUserDTO(userDTO, user);
        userRepository.update(user);
    }
}
