package com.senla.task1.controller;

import com.senla.task1.dto.AuthenticationDTO;
import com.senla.task1.dto.UserDTO;
import com.senla.task1.service.UserService;
import com.senla.task1.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public AuthController(JWTUtil jwtUtil, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public Map<String, String> signUp(@RequestBody UserDTO userDTO) {
        userService.saveUser(userDTO);
        String token = jwtUtil.generateToken(userDTO.username());
        return Map.of("token", token);
    }

    @PostMapping("/auth/login")
    public Map<String, String> login(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.username(),
                        authenticationDTO.password());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Incorrect username or password!");
        }

        String token = jwtUtil.generateToken(authenticationDTO.username());
        return Map.of("jwt-token", token);
    }
}
