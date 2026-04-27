package com.senla.UserService.controller;

import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<UserDTO> getUserProfile() {
        Long userId = userService.getPrincipalId();
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @PatchMapping(value = "/profile")
    public ResponseEntity<UserDTO> updateUserProfile(@Valid @RequestBody UserDTO userDTO) {
        Long userId = userService.getPrincipalId();
        userService.update(userId, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }
}
