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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    public AuthServiceImpl(UserService userService, JwtUtil jwtUtil, UserMapper userMapper, RoleService roleService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
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
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userService.findByUsername(authRequest.username());

        return new JwtResponse(
                jwtUtil.generateAccessToken(user),
                jwtUtil.generateRefreshToken(user)
        );
    }

    @Override
    @Transactional
    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtUtil.validateRefreshToken(refreshToken)) {
            Claims claims = jwtUtil.getRefreshClaims(refreshToken);
            String username = claims.getSubject();
            String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findByUsername(username);
                String accessToken = jwtUtil.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    @Override
    @Transactional
    public JwtResponse refresh(String refreshToken) {
        if (jwtUtil.validateRefreshToken(refreshToken)) {
            Claims claims = jwtUtil.getRefreshClaims(refreshToken);
            String username = claims.getSubject();
            String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.findByUsername(username);
                String accessToken = jwtUtil.generateAccessToken(user);
                String newRefreshToken = jwtUtil.generateRefreshToken(user);
                refreshStorage.put(user.getUsername(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Invalid refresh token");
    }

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        User user = userMapper.registerRequestToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRegistrationDate(LocalDate.now());
        Role role = roleService.findByName(RoleEnum.ROLE_USER.getDisplayName());
        user.setRole(role);
        userService.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        refreshStorage.put(user.getUsername(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }
}
