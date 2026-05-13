package com.senla.UserService.service.impl;

import com.senla.UserService.dto.AuthRequest;
import com.senla.UserService.dto.JwtResponse;
import com.senla.UserService.dto.RegisterRequest;
import com.senla.UserService.exception.AuthException;
import com.senla.UserService.mapper.UserMapper;
import com.senla.UserService.model.Role;
import com.senla.UserService.model.User;
import com.senla.UserService.model.enums.RoleEnum;
import com.senla.UserService.service.RoleService;
import com.senla.UserService.service.UserService;
import com.senla.UserService.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @Spy
    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(RoleEnum.ROLE_USER.getDisplayName());
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(role);
    }

    @Test
    void loginShouldReturnJwtResponse() {
        AuthRequest authRequest = new AuthRequest("username", "password");

        when(userService.findByUsername("username")).thenReturn(user);
        when(jwtUtil.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refreshToken");

        JwtResponse jwtResponse = authServiceImpl.login(authRequest);

        assertNotNull(jwtResponse);
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void loginShouldThrowBadCredentialsException() {
        AuthRequest authRequest = new AuthRequest("username", "password");

        doThrow(new BadCredentialsException("exception")).when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authServiceImpl.login(authRequest));
    }

    @Test
    void getAccessTokenShouldReturnJwtResponseWithData() {
        String refreshToken = "refreshToken";
        Claims claims = mock(Claims.class);

        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getRefreshClaims(refreshToken)).thenReturn(claims);

        when(claims.getSubject()).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(user);
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("accessToken");
        authServiceImpl.saveRefreshToken(user.getUsername(), refreshToken);

        JwtResponse jwtResponse = authServiceImpl.getAccessToken(refreshToken);

        assertNotNull(jwtResponse);
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertNull(jwtResponse.getRefreshToken());
    }

    @Test
    void getAccessTokenShouldReturnJwtResponseWithNoData() {
        String refreshToken = "refreshToken";

        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        JwtResponse jwtResponse = authServiceImpl.getAccessToken(refreshToken);

        assertNull(jwtResponse.getAccessToken());
    }

    @Test
    void refreshShouldReturnJwtResponse() {
        String refreshToken = "refreshToken";
        String accessToken = "accessToken";
        String newRefreshToken = "newRefreshToken";
        Claims claims = mock(Claims.class);

        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getRefreshClaims(refreshToken)).thenReturn(claims);

        when(claims.getSubject()).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(user);
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn(newRefreshToken);
        authServiceImpl.saveRefreshToken(user.getUsername(), refreshToken);

        JwtResponse jwtResponse = authServiceImpl.refresh(refreshToken);

        assertNotNull(jwtResponse);
        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(newRefreshToken, jwtResponse.getRefreshToken());
    }

    @Test
    void refreshShouldThrowAuthException() {
        String refreshToken = "refreshToken";

        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        assertThrows(AuthException.class, () -> authServiceImpl.refresh(refreshToken));
    }

    @Test
    void registerShouldReturnJwtResponse() {
        RegisterRequest registerRequest =
                new RegisterRequest("username", "79999999999", "password");

        when(userService.findByUsernameOrNull(registerRequest.username())).thenReturn(null);
        when(userService.findByPhoneNumberIfExists(registerRequest.phoneNumber())).thenReturn(null);

        when(userMapper.registerRequestToUser(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(registerRequest.password());
        when(roleService.findByName(RoleEnum.ROLE_USER.getDisplayName())).thenReturn(role);
        when(jwtUtil.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refreshToken");

        JwtResponse jwtResponse = authServiceImpl.register(registerRequest);

        assertNotNull(jwtResponse);
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void registerShouldThrowEntityExistsExceptionWithUsername() {
        RegisterRequest registerRequest =
                new RegisterRequest("username", "79999999999", "password");

        when(userService.findByUsernameOrNull(registerRequest.username())).thenReturn(user);

        assertThrows(EntityExistsException.class, () -> authServiceImpl.register(registerRequest));
    }

    @Test
    void registerShouldThrowEntityExistsExceptionWithPhoneNumber() {
        RegisterRequest registerRequest =
                new RegisterRequest("username", "79999999999", "password");

        when(userService.findByPhoneNumberIfExists(registerRequest.phoneNumber())).thenReturn(user);

        assertThrows(EntityExistsException.class, () -> authServiceImpl.register(registerRequest));
    }
}