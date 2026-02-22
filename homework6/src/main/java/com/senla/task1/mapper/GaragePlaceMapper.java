package com.senla.task1.mapper;

import com.senla.task1.dto.GaragePlaceDTO;
import com.senla.task1.models.GaragePlace;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GaragePlaceMapper {

    public abstract GaragePlaceDTO garagePlaceToGaragePlaceDTO(GaragePlace garagePlace);
}
