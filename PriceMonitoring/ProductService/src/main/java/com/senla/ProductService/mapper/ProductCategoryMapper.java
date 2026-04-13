package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.ProductCategoryDTO;
import com.senla.ProductService.model.ProductCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProductCategoryMapper {

    public abstract ProductCategory productCategoryDTOToProductCategory(ProductCategoryDTO productCategoryDTO);

    public abstract ProductCategoryDTO productCategoryToProductCategoryDTO(ProductCategory productCategory);
}
