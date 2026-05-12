package com.senla.UserService.service.impl;

import com.senla.UserService.dto.AuthRequest;
import com.senla.UserService.dto.JwtResponse;
import com.senla.UserService.dto.RegisterRequest;
import com.senla.UserService.exception.AuthException;
import com.senla.UserService.mapper.UserMapper;
import com.senla.UserService.model.Role;
import com.senla.UserService.model.User;
import com.senla.UserService.model.enums.RoleEnum;
import com.senla.UserService.service.AuthService;
import com.senla.UserService.service.RoleService;
import com.senla.UserService.service.UserService;
import com.senla.UserService.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    public AuthServiceImpl(UserService userService, JwtUtil jwtUtil,
                           UserMapper userMapper, RoleService roleService,
                           PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }


    @Override
    @Transactional
    public JwtResponse login(AuthRequest authRequest) throws AuthException {
        logger.info("Login request {}", authRequest);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());

        try {
            logger.info("Attempting to authenticate user {}", authRequest.username());
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials, invalid username or password {}", authRequest);
            throw new BadCredentialsException("Invalid username or password!");
        }
        logger.info("Authenticated user {}", authRequest.username());
        User user = userService.findByUsername(authRequest.username());
        logger.info("Found user {}", user);
        return new JwtResponse(
                jwtUtil.generateAccessToken(user),
                jwtUtil.generateRefreshToken(user)
        );
    }

    @Override
    @Transactional
    public JwtResponse getAccessToken(String refreshToken) {
        logger.info("Refresh access token with refresh token {}", refreshToken);
        if (jwtUtil.validateRefreshToken(refreshToken)) {
            logger.info("Valid refresh token {}", refreshToken);
            Claims claims = jwtUtil.getRefreshClaims(refreshToken);
            String username = claims.getSubject();
            String saveRefreshToken = refreshStorage.get(username);
            logger.info("Saved refresh token {}", saveRefreshToken);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findByUsername(username);
                String accessToken = jwtUtil.generateAccessToken(user);
                logger.info("Saved and return access token for user {}", username);
                return new JwtResponse(accessToken, null);
            }
        }
        logger.info("Invalid refresh token {}", refreshToken);
        return new JwtResponse(null, null);
    }

    @Override
    @Transactional
    public JwtResponse refresh(String refreshToken) {
        logger.info("Get new refresh token {}", refreshToken);
        if (jwtUtil.validateRefreshToken(refreshToken)) {
            logger.info("Valid refresh token {}", refreshToken);
            Claims claims = jwtUtil.getRefreshClaims(refreshToken);
            String username = claims.getSubject();
            String saveRefreshToken = refreshStorage.get(username);
            logger.info("Saved refresh token {}", saveRefreshToken);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findByUsername(username);
                String accessToken = jwtUtil.generateAccessToken(user);
                String newRefreshToken = jwtUtil.generateRefreshToken(user);
                refreshStorage.put(user.getUsername(), newRefreshToken);
                logger.info("Saved refresh and access token for user {} ", username);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        logger.warn("Invalid refresh token {}", refreshToken);
        throw new AuthException("Invalid refresh token");
    }

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        logger.info("Register request {}", registerRequest);
        if (userService.findByUsernameOrNull(registerRequest.username()) != null) {
            logger.warn("Trying to register with username {} that already exists", registerRequest.username());
            throw new EntityExistsException("User with username " + registerRequest.username() + " already exists");
        }
        if (userService.findByPhoneNumberIfExists(registerRequest.phoneNumber()) != null) {
            logger.warn("Trying to register with phone number {} that already exists", registerRequest.phoneNumber());
            throw new EntityExistsException("Phone number " + registerRequest.phoneNumber() + " already exists");
        }

        User user = userMapper.registerRequestToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRegistrationDate(LocalDate.now());
        Role role = roleService.findByName(RoleEnum.ROLE_USER.getDisplayName());
        user.setRole(role);
        userService.save(user);
        logger.info("Saved user {}", user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        refreshStorage.put(user.getUsername(), refreshToken);
        logger.info("Return refresh and access token for user {}", user.getUsername());
        return new JwtResponse(accessToken, refreshToken);
    }
}
