package com.senla.ProductService.mapper;

import com.senla.ProductService.dto.subscription.SubscriptionDTO;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class SubscriptionMapper {

    @Mapping(target = "productId", source = "productPrice.product.id")
    @Mapping(target = "productName", source = "productPrice.product.name")
    @Mapping(target = "shopBranchId", source = "productPrice.shopBranch.id")
    @Mapping(target = "shopName", source = "productPrice.shopBranch.shop.name")
    @Mapping(target = "shopAddress", source = "productPrice.shopBranch", qualifiedByName = "getShopAddress")
    @Mapping(target = "price", source = "productPrice.price")
    @Mapping(target = "startDate", source = "productPrice.startDate", dateFormat = "yyyy-MM-dd")
    public abstract SubscriptionDTO subscriptionToSubscriptionDTO(Subscription subscription);

    public abstract Subscription subscriptionDTOToSubscription(SubscriptionDTO subscriptionDTO);

    @Named("getShopAddress")
    protected String getShopAddress(ShopBranch shopBranch) {
        if(shopBranch == null) {
            return null;
        }

        return String.format("%s %s %s", shopBranch.getStreet(), shopBranch.getHouse(), shopBranch.getRoom());
    }
}
