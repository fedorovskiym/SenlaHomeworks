package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.CreateProductPriceDTO;
import com.senla.ProductService.dto.ProductPriceDTO;
import com.senla.ProductService.model.ProductPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProductPriceMapper {

    public abstract ProductPrice createProductPriceDTOToProductPrice(CreateProductPriceDTO  createProductPriceDTO);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "product")
    public abstract ProductPriceDTO productPriceToProductPriceDTO(ProductPrice productPrice);
}
