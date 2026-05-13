package com.senla.UserService.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.UserService.broker.KafkaBroker;
import com.senla.UserService.config.TestConfiguration;
import com.senla.UserService.dto.KafkaMessageWithUser;
import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.mapper.UserMapper;
import com.senla.UserService.model.Role;
import com.senla.UserService.model.User;
import com.senla.UserService.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jose4j.jwk.Use;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestConfiguration.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private KafkaBroker kafkaBroker;

    @InjectMocks
    private UserServiceImpl userService;

    private Role role;
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_USER");
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("username");
        user.setPassword("password");
        user.setPhoneNumber("79999999999");
        user.setRole(role);
        user.setRegistrationDate(LocalDate.now());

        userDTO = new UserDTO(user.getId(), user.getUsername(), user.getPhoneNumber(), user.getRegistrationDate());
    }

    @Test
    void findByUsernameShouldReturnUser() {
        String username = "username";

        when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        User result = userService.findByUsername(username);

        assertEquals(user, result);
    }

    @Test
    void findByUsernameShouldThrowEntityNotFoundException() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername(user.getUsername()));
    }

    @Test
    void findByPhoneNumberIfExistsShouldReturnUser() {
        String phoneNumber = "79999999999";

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.ofNullable(user));

        User result = userService.findByPhoneNumberIfExists(phoneNumber);

        assertEquals(user, result);
    }

    @Test
    void findByPhoneNumberIfExistsShouldReturnNull() {
        String phoneNumber = "79999999999";

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        User result = userService.findByPhoneNumberIfExists(phoneNumber);

        assertNull(result);
    }

    @Test
    void saveShouldCallUserRepositorySaveMethod() {
        KafkaMessageWithUser kafkaMessageWithUser = new KafkaMessageWithUser(user.getId(), user.getPhoneNumber());

        when(userMapper.userToKafkaMessage(user)).thenReturn(kafkaMessageWithUser);

        userService.save(user);

        verify(userRepository).save(user);
        verify(userMapper).userToKafkaMessage(user);
        verify(kafkaBroker).sendMessageWithNewUser(eq(user.getId()), anyString());
    }

    @Test
    void getPrincipalId() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UUID result = userService.getPrincipalId();

        assertEquals(user.getId(), result);
    }

    @Test
    void getUserByIdShouldReturnUserDTO() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(user.getId());

        assertEquals(userDTO, result);

        verify(userRepository).findById(user.getId());
        verify(userMapper).userToUserDTO(user);
    }

    @Test
    void updateShouldCallRepositoryUpdateMethodAndSendKafkaMessage() {
        userDTO = new UserDTO(user.getId(), user.getUsername(), "newPhoneNumber", user.getRegistrationDate());

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(userMapper.updateUserFromUserDTO(userDTO, user)).thenReturn(user);

        userService.update(user.getId(), userDTO);

        verify(userMapper).updateUserFromUserDTO(userDTO, user);
        verify(userRepository).update(user);
        verify(kafkaBroker).sendMessageWithUpdateUser(eq(user.getId()), anyString());
    }

    @Test
    void updateShouldCallOnlyRepositoryUpdateMethod() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(userMapper.updateUserFromUserDTO(userDTO, user)).thenReturn(user);

        userService.update(user.getId(), userDTO);

        verify(userMapper).updateUserFromUserDTO(userDTO, user);
        verify(userRepository).update(user);
        verify(kafkaBroker, never()).sendMessageWithUpdateUser(eq(user.getId()), anyString());
    }

    @Test
    void findAllShouldReturnListDTO() {
        List<User> users = List.of(user);
        List<UserDTO> userDTOs = List.of(userDTO);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.findAll();

        assertEquals(userDTOs, result);
        verify(userRepository).findAll();
        verify(userMapper).userToUserDTO(user);
    }

    @Test
    void deleteShouldDeleteUserAndSendKafkaMessage() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        verify(userRepository).delete(user);
        verify(kafkaBroker).sendMessageWithDeleteUser(user.getId(), user.getId().toString());
    }

    @Test
    void findByIdIfExistsShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.findByIdIfExists(user.getId());

        assertEquals(user, result);
        verify(userRepository).findById(user.getId());
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByIdIfExists(user.getId()));
    }

    @Test
    void findByUsernameOrNullShouldReturnUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User result = userService.findByUsernameOrNull(user.getUsername());

        assertEquals(user, result);
    }

    @Test
    void findByUsernameOrNullShouldReturnNull() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        User result = userService.findByUsernameOrNull(user.getUsername());

        assertNull(result);
    }
}