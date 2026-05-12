package com.senla.UserService.service.impl;

import com.senla.UserService.broker.KafkaBroker;
import com.senla.UserService.dto.KafkaMessageWithUser;
import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.mapper.UserMapper;
import com.senla.UserService.model.User;
import com.senla.UserService.repository.UserRepository;
import com.senla.UserService.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaBroker kafkaBroker;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, KafkaBroker kafkaBroker) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.kafkaBroker = kafkaBroker;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        logger.info("Finding user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> {
            logger.warn("User not found with username {}", username);
            return new EntityNotFoundException("User not found with username: " + username);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public User findByPhoneNumberIfExists(String phoneNumber) {
        logger.info("Finding user by phone number if exists {}", phoneNumber);
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    @Override
    @Transactional
    public void save(User user) {
        logger.info("Saving user {}", user);
        userRepository.save(user);
        logger.info("Successfully saved user {}", user);
        KafkaMessageWithUser message = userMapper.userToKafkaMessage(user);
        String json = objectMapper.writeValueAsString(message);
        kafkaBroker.sendMessageWithNewUser(user.getId(), json);
    }

    @Override
    @Transactional
    public UUID getPrincipalId() {
        logger.info("Getting principal id");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Find principal {}", principal);
        return findByUsername(principal.getUsername()).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        logger.info("Getting user with id {}", id);
        return userMapper.userToUserDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional
    public void update(UUID userId, UserDTO userDTO) {
        logger.info("Updating user with id {}", userId);
        User user = findByIdIfExists(userId);

        if (!user.getPhoneNumber().equals(userDTO.phoneNumber())) {
            KafkaMessageWithUser message = new KafkaMessageWithUser(userId, userDTO.phoneNumber());
            String json = objectMapper.writeValueAsString(message);
            kafkaBroker.sendMessageWithUpdateUser(user.getId(), json);
        }

        logger.info("Updating user with id {} from userDTO {}", userId, userDTO);
        user = userMapper.updateUserFromUserDTO(userDTO, user);
        userRepository.update(user);
        logger.info("Successfully updated user with id {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        logger.info("Finding all users");
        return userRepository.findAll().stream().map(userMapper::userToUserDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        logger.info("Deleting user with id {}", id);
        User user = findByIdIfExists(id);
        userRepository.delete(user);
        logger.info("Successfully deleted user with id {}", id);
        kafkaBroker.sendMessageWithDeleteUser(id, id.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public User findByIdIfExists(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
            logger.warn("User not found with id {}", id);
            return new EntityNotFoundException("User with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsernameOrNull(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
