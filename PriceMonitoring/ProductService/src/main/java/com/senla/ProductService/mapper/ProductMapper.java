package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    public abstract Product productDTOToProduct(ProductDTO productDTO);

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryId", source = "productCategory.id")
    @Mapping(target = "categoryName", source = "productCategory.name")
    public abstract ProductDTO productToProductDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Product updateProductFromDTO(ProductUpdateDTO productUpdateDTO, @MappingTarget Product product);
}
