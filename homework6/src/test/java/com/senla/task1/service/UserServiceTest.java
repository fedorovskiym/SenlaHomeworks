package com.senla.task1.service;

import com.senla.task1.dto.UserDTO;
import com.senla.task1.mapper.UserMapper;
import com.senla.task1.models.Role;
import com.senla.task1.models.User;
import com.senla.task1.models.enums.RoleEnum;
import com.senla.task1.repository.RoleRepository;
import com.senla.task1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveUser_Success() {
        UserDTO userDTO = new UserDTO(1, "username", "password");
        Role role = new Role(1, RoleEnum.ROLE_USER);
        User user = new User();

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.empty());
        when(userMapper.userDTOToUser(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        userService.saveUser(userDTO);

        verify(userRepository).save(user);
        verify(passwordEncoder).encode(userDTO.password());

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    void saveUser_ThrowHttpStatusConflict() {
        UserDTO userDTO = new UserDTO(1, "username", "password");

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(new User()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.saveUser(userDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Username is already in use", exception.getReason());

        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ThrowRuntimeException() {
        UserDTO userDTO = new UserDTO(1, "username", "password");
        Role role = new Role(1, RoleEnum.ROLE_USER);
        User user = new User();

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.empty());
        when(userMapper.userDTOToUser(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.saveUser(userDTO));

        assertEquals("Роль не найдена", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}