package com.senla.UserService.mapper;

import com.senla.UserService.dto.RegisterRequest;
import com.senla.UserService.dto.UserDTO;
import com.senla.UserService.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract User registerRequestToUser(RegisterRequest registerRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract User updateUserFromUserDTO(UserDTO userDTO, @MappingTarget User user);

    public abstract UserDTO userToUserDTO(User user);
}
