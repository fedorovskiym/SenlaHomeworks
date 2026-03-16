package com.senla.task1.service;

import com.senla.task1.dto.UserDTO;
import com.senla.task1.mapper.UserMapper;
import com.senla.task1.models.Role;
import com.senla.task1.models.User;
import com.senla.task1.models.enums.RoleEnum;
import com.senla.task1.repository.RoleRepository;
import com.senla.task1.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void saveUser(UserDTO userDTO) {
        logger.info("Обработка регистрации пользователя {}", userDTO);
        if (userRepository.findByUsername(userDTO.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }
        User user = userMapper.userDTOToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        Role role = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException(
                "Роль не найдена"
        ));
        user.setRole(role);
        userRepository.save(user);
        logger.info("Пользователь {} зарегистрировался", user);
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        logger.info("Поиск пользователя по логину {}", username);
        return userRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " not found"));
    }
}
