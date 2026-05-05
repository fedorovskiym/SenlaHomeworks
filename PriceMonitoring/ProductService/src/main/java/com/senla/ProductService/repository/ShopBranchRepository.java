package com.senla.ProductService.repository;

import com.senla.ProductService.model.ShopBranch;

import java.util.List;
import java.util.UUID;

public interface ShopBranchRepository extends GenericRepository<ShopBranch, UUID> {

    List<ShopBranch> findAllByShopIdFetch(UUID shopId);
}
