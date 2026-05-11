package com.senla.UserService.service;

import com.senla.UserService.dto.AuthRequest;
import com.senla.UserService.dto.JwtResponse;
import com.senla.UserService.dto.RegisterRequest;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * inteface for working with autnentication
 */
public interface AuthService {

    /**
     * method for login users
     *
     * @param authRequest contains data for login
     * @return JwtResponse with access token, refresh token and type bearer
     * @throws BadCredentialsException if auth request data invalid
     */
    JwtResponse login(AuthRequest authRequest);

    /**
     * method for getting new access token from refresh token
     *
     * @param refreshToken contains refresh token
     * @return JwtResponse with access token, refresh token and type bearer
     */
    JwtResponse getAccessToken(String refreshToken);

    /**
     * method for getting new refresh token from old refresh token
     *
     * @param refreshToken contains old refresh token
     * @return JwtResponse with access token, refresh token and type bearer
     */
    JwtResponse refresh(String refreshToken);

    /**
     * method for registration users
     *
     * @param registerRequest contains registration data
     * @return JwtResponse with access token, refresh token and type bearer
     * @throws EntityExistsException if user with username or phone number from data already exists
     */
    JwtResponse register(RegisterRequest registerRequest);
}
