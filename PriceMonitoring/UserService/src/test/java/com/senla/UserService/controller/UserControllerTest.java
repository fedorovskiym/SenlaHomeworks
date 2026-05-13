package com.senla.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.model.User;
import com.senla.UserService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("username");
        user.setPassword("password");
        user.setPhoneNumber("79999999999");
        user.setRegistrationDate(LocalDate.now());
        userDTO = new UserDTO(user.getId(), user.getUsername(), user.getPhoneNumber(), user.getRegistrationDate());
    }

    @Test
    void getUserProfileShouldReturnUserDTO() throws Exception {
        when(userService.getPrincipalId()).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(userDTO);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    UserDTO response = objectMapper.readValue(jsonResponse, UserDTO.class);

                    assertEquals(userDTO.id(), response.id());
                    assertEquals(userDTO.username(), response.username());
                    assertEquals(userDTO.phoneNumber(), response.phoneNumber());
                    assertEquals(userDTO.registrationDate(), response.registrationDate());
                });

        verify(userService).getPrincipalId();
        verify(userService).getUserById(user.getId());
    }

    @Test
    void updateUserProfileShouldReturnUpdatedUserDTO() throws Exception {
        UserDTO updateRequest = new UserDTO(null, null, "71111111111", null);
        UserDTO updatedUserDTO = new UserDTO(user.getId(), user.getUsername(),
                "71111111111", user.getRegistrationDate());

        when(userService.getPrincipalId()).thenReturn(user.getId());
        doNothing().when(userService).update(user.getId(), updateRequest);
        when(userService.getUserById(user.getId())).thenReturn(updatedUserDTO);

        mockMvc.perform(patch("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    UserDTO response = objectMapper.readValue(jsonResponse, UserDTO.class);

                    assertEquals(updatedUserDTO.id(), response.id());
                    assertEquals(updatedUserDTO.username(), response.username());
                    assertEquals(updatedUserDTO.phoneNumber(), response.phoneNumber());
                    assertEquals(updatedUserDTO.registrationDate(), response.registrationDate());
                });

        verify(userService).getPrincipalId();
        verify(userService).update(user.getId(), updateRequest);
        verify(userService).getUserById(user.getId());
    }

    @Test
    void getAllUsersShouldReturnListOfUserDTO() throws Exception {
        List<UserDTO> users = List.of(userDTO);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/user/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<UserDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, UserDTO.class));

                    assertEquals(1, response.size());
                });

        verify(userService).findAll();
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        UUID userIdToDelete = UUID.randomUUID();

        doNothing().when(userService).delete(userIdToDelete);

        mockMvc.perform(delete("/user/{id}", userIdToDelete))
                .andExpect(status().isNoContent());

        verify(userService).delete(userIdToDelete);
    }
}