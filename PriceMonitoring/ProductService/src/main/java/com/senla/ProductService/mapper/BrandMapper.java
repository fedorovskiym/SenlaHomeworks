package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.model.Brand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class BrandMapper {

    public abstract Brand brandDTOToBrand(BrandDTO brandDTO);

    public abstract BrandDTO brandToBrandDTO(Brand brand);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Brand updateBrandFromBrandUpdateDTO(BrandUpdateDTO brandDTO, @MappingTarget Brand brand);
}
