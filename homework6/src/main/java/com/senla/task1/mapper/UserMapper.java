package com.senla.task1.mapper;

import com.senla.task1.dto.UserDTO;
import com.senla.task1.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract User userDTOToUser(UserDTO userDTO);
}
