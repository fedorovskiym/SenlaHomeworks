package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.ShopBranch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class ProductPriceMapper {

    public abstract ProductPrice createProductPriceDTOToProductPrice(CreateProductPriceDTO  createProductPriceDTO);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productAmount", source = "product.amount")
    @Mapping(target = "productUnit", source = "product.unit")
    @Mapping(target = "productCategoryId", source = "product.productCategory.id")
    @Mapping(target = "productCategoryName", source = "product.productCategory.name")
    @Mapping(target = "shopBranchId", source = "shopBranch.id")
    @Mapping(target = "shopAddress", source = "shopBranch", qualifiedByName = "getShopAddress")
    @Mapping(target = "shopName", source = "shopBranch.shop.name")
    @Mapping(target = "cityId", source = "shopBranch.city.id")
    @Mapping(target = "cityName", source = "shopBranch.city.name")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "dd-MM-yyyy")
    @Mapping(target = "status", source = "status.displayName")
    public abstract ProductPriceDTO productPriceToProductPriceDTO(ProductPrice productPrice);

    @Named("getShopAddress")
    protected String getShopAddress(ShopBranch shopBranch) {
        if(shopBranch == null) {
            return null;
        }

        return String.format("%s %s %s", shopBranch.getStreet(), shopBranch.getHouse(), shopBranch.getRoom());
    }
}
