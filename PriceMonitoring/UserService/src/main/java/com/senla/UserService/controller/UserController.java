package com.senla.UserService.controller;

import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<UserDTO> getUserProfile() {
        logger.info("Recieved get user profile request /api/user-service/user/profile");
        UUID userId = userService.getPrincipalId();
        logger.info("Return user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @PatchMapping(value = "/profile")
    public ResponseEntity<UserDTO> updateUserProfile(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Recieved update user profile request /api/user-service/user/profile");
        UUID userId = userService.getPrincipalId();
        userService.update(userId, userDTO);
        logger.info("Update user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @GetMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Recieved get all users request /api/user-service/user/");
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable UUID id) {
        logger.info("Recieved delete user request /api/user-service/user/{}", id);
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
