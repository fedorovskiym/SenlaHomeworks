package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.model.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ShopMapper {

    public abstract ShopDTO shopToShopDTO(Shop shop);

    public abstract Shop shopDTOToShop(ShopDTO shopDTO);
}
