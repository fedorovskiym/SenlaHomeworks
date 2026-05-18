package com.senla.ProductService.repository;

import com.senla.ProductService.model.ShopBranch;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShopBranchRepository extends GenericRepository<ShopBranch, UUID> {

    List<ShopBranch> findAllByCityIdAndShopIdFetch(UUID cityId, UUID shopId);

    Map<UUID, ShopBranch> findAllById(Set<UUID> listShopBranchId);
}
