package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.model.ShopBranch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ShopBranchMapper {

    public abstract ShopBranch shopBranchDTOToShopBranch(ShopBranchDTO shopBranchDTO);

    @Mapping(target = "shopId", source = "shop.id")
    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "shopName", source = "shop.name")
    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "logoImageUrl", source = "shop.logoImageUrl")
    public abstract ShopBranchDTO shopBranchToShopBranchDTO(ShopBranch shopBranch);
}
