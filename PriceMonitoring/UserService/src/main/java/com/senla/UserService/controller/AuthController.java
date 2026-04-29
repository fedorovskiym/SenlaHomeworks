package com.senla.UserService.controller;

import com.senla.UserService.dto.AuthRequest;
import com.senla.UserService.dto.JwtResponse;
import com.senla.UserService.dto.RefreshJwtRequest;
import com.senla.UserService.dto.RegisterRequest;
import com.senla.UserService.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("Recieved login request /api/user-service/auth/login");
        JwtResponse token = authService.login(authRequest);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity<JwtResponse> signUp(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Recieved signUp request /api/user-service/auth/signUp");
        JwtResponse token = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping(value = "/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest refreshJwtRequest) {
        logger.info("Recieved refresh access token request /api/user-service/auth/token");
        JwtResponse token = authService.getAccessToken(refreshJwtRequest.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest refreshJwtRequest) {
        logger.info("Recieved get new refresh token request /api/user-service/auth/token");
        JwtResponse token = authService.refresh(refreshJwtRequest.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
