package com.senla.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.UserService.dto.AuthRequest;
import com.senla.UserService.dto.JwtResponse;
import com.senla.UserService.dto.RefreshJwtRequest;
import com.senla.UserService.dto.RegisterRequest;
import com.senla.UserService.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
    }

    @Test
    void loginShouldReturnJwtResponse() throws Exception {
        AuthRequest authRequest = new AuthRequest("username", "password");
        JwtResponse expected = new JwtResponse("accessToken", "refreshToken");

        when(authService.login(authRequest)).thenReturn(expected);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    JwtResponse actual = objectMapper.readValue(jsonResponse, JwtResponse.class);

                    assertEquals(expected.getAccessToken(), actual.getAccessToken());
                    assertEquals(expected.getRefreshToken(), actual.getRefreshToken());
                });

        verify(authService, times(1)).login(authRequest);
    }

    @Test
    void signUpShouldReturnJwtResponse() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "79999999999",
                "password");
        JwtResponse expected = new JwtResponse("accessToken", "refreshToken");

        when(authService.register(registerRequest)).thenReturn(expected);

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    JwtResponse actual = objectMapper.readValue(jsonResponse, JwtResponse.class);

                    assertEquals(expected.getAccessToken(), actual.getAccessToken());
                    assertEquals(expected.getRefreshToken(), actual.getRefreshToken());
                });

        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void getNewAccessTokenShouldReturnJwtResponse() throws Exception {
        RefreshJwtRequest refreshRequest = new RefreshJwtRequest("refreshToken");
        JwtResponse expected = new JwtResponse("newAccessToken", null);

        when(authService.getAccessToken(refreshRequest.refreshToken())).thenReturn(expected);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    JwtResponse actual = objectMapper.readValue(jsonResponse, JwtResponse.class);

                    assertEquals(expected.getAccessToken(), actual.getAccessToken());
                    assertEquals(expected.getRefreshToken(), actual.getRefreshToken());
                });

        verify(authService, times(1)).getAccessToken(refreshRequest.refreshToken());
    }

    @Test
    void getNewRefreshTokenShouldReturnJwtResponse() throws Exception {
        RefreshJwtRequest refreshRequest = new RefreshJwtRequest("oldRefreshToken");
        JwtResponse expected = new JwtResponse("accessToken", "newRefreshToken");

        when(authService.refresh(refreshRequest.refreshToken())).thenReturn(expected);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    JwtResponse actual = objectMapper.readValue(jsonResponse, JwtResponse.class);

                    assertEquals(expected.getAccessToken(), actual.getAccessToken());
                    assertEquals(expected.getRefreshToken(), actual.getRefreshToken());
                });

        verify(authService, times(1)).refresh(refreshRequest.refreshToken());
    }
}