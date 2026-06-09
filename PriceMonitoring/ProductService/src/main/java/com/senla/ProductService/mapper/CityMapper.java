package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.model.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CityMapper {

    public abstract City cityDTOToCity(CityDTO cityDTO);

    public abstract CityDTO cityToCityDTO(City city);
}
