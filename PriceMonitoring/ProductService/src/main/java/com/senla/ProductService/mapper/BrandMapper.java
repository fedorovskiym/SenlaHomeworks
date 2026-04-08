package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.BrandDTO;
import com.senla.ProductService.model.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class BrandMapper {

    public abstract Brand brandDTOToBrand(BrandDTO brandDTO);

    public abstract BrandDTO brandToBrandDTO(Brand brand);
}
