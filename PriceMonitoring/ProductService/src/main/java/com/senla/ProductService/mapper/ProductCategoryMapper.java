package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.model.ProductCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class ProductCategoryMapper {

    public abstract ProductCategory productCategoryDTOToProductCategory(ProductCategoryDTO productCategoryDTO);

    public abstract ProductCategoryDTO productCategoryToProductCategoryDTO(ProductCategory productCategory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract ProductCategory updateProductCategoryFromDTO(ProductCategoryUpdateDTO productCategoryUpdateDTO,
                                                                 @MappingTarget ProductCategory productCategory);
}
