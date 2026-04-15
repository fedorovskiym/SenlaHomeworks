package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.model.ProductPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProductPriceMapper {

    public abstract ProductPrice createProductPriceDTOToProductPrice(CreateProductPriceDTO  createProductPriceDTO);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productAmount", source = "product.amount")
    @Mapping(target = "productUnit", source = "product.unit")
    @Mapping(target = "shopBranchId", source = "shopBranch.id")
    @Mapping(target = "shopName", source = "shopBranch.shop.name")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "dd-MM-yyyy")
    public abstract ProductPriceDTO productPriceToProductPriceDTO(ProductPrice productPrice);
}
