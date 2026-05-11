package com.senla.UserService.service;

import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.model.User;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * interface for wokring with users
 */
public interface UserService {

    /**
     * method for finding user by username
     *
     * @param username contains username
     * @return user with username from request
     * @throws EntityNotFoundException if user with username from request not found
     */
    User findByUsername(String username);

    /**
     * method for finding user by phone number or null
     *
     * @param phoneNumber contains phone number
     * @return user with phone number from request
     */
    User findByPhoneNumberIfExists(String phoneNumber);

    /**
     * method for saving user
     *
     * @param user contains user data for saving
     */
    void save(User user);

    /**
     * Method for getting the authenticated user's id.
     *
     * @return authenticated user UUID
     */
    UUID getPrincipalId();

    /**
     * method for getting user dto by id
     *
     * @param id from request to find user
     * @return userDTO mapped from user with id from request
     */
    UserDTO getUserById(UUID id);

    /**
     * method for updating user by id
     *
     * @param userId from request to update user
     * @param userDTO contains data to update user
     */
    void update(UUID userId, UserDTO userDTO);

    /**
     * method for finding list user dto
     *
     * @return list userDTO mapped from list user
     */
    List<UserDTO> findAll();

    /**
     * method for deleting user by id
     *
     * @param id from request to delete user by id
     */
    void delete(UUID id);

    /**
     * method for finding user by id
     *
     * @param id from request to find user by id
     * @return user with id from request
     * @throws EntityNotFoundException if user with id from request not found
     */
    User findByIdIfExists(UUID id);
}
