package com.senla.UserService.mapper;

import com.senla.UserService.dto.RegisterRequest;
import com.senla.UserService.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract User registerRequestToUser(RegisterRequest registerRequest);
}
