package com.senla.task1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.task1.dto.AuthenticationDTO;
import com.senla.task1.dto.UserDTO;
import com.senla.task1.service.UserService;
import com.senla.task1.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(jwtUtil, authenticationManager, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void signUp_ReturnTokenAndStatusOk() throws Exception {
        UserDTO userDTO = new UserDTO(1,"username", "password");

        when(jwtUtil.generateToken(userDTO.username())).thenReturn("testToken");

        mockMvc.perform(post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"token\":\"testToken\"}"));

        verify(userService).saveUser(userDTO);
    }

    @Test
    void login_ReturnJwtTokenAndStatusOk() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("username", "password");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("username", "password"));
        when(jwtUtil.generateToken(authDTO.username())).thenReturn("testToken");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"jwt-token\":\"testToken\"}"));

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_BadCredentialsAndReturnMessage() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO("username", "password");

        doThrow(new RuntimeException("Bad credentials")).when(authenticationManager).authenticate(any());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"Incorrect username or password!\"}"));

        verify(authenticationManager).authenticate(any());
    }
}