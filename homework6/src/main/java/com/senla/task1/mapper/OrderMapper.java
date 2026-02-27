package com.senla.task1.mapper;

import com.senla.task1.dto.OrderDTO;
import com.senla.task1.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Mapping(target = "mechanicName", source = "mechanic.name")
    @Mapping(target = "mechanicSurname", source = "mechanic.surname")
    @Mapping(target = "mechanicId", source = "mechanic.id")
    @Mapping(target = "placeNumber", source = "garagePlace.placeNumber")
    @Mapping(target = "status", source = "status.name")
    public abstract OrderDTO orderToOrderDTO(Order order);
}
