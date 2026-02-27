package com.senla.task1.mapper;

import com.senla.task1.dto.MechanicDTO;
import com.senla.task1.models.Mechanic;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class MechanicMapper {

    public abstract MechanicDTO mechanicToMechanicDTO(Mechanic mechanic);
}
