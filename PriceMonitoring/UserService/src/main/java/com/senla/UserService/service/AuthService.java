package com.senla.UserService.service;

import com.senla.UserService.dto.AuthRequest;
import com.senla.UserService.dto.JwtResponse;
import com.senla.UserService.dto.RegisterRequest;

public interface AuthService {

    JwtResponse login(AuthRequest authRequest);

    JwtResponse getAccessToken(String refreshToken);

    JwtResponse refresh(String refreshToken);

    JwtResponse register(RegisterRequest registerRequest);
}
