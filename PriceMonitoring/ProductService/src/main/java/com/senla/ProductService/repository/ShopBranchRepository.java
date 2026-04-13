package com.senla.ProductService.repository;

import com.senla.ProductService.model.ShopBranch;

import java.util.List;

public interface ShopBranchRepository extends GenericRepository<ShopBranch, Long> {

    List<ShopBranch> findAllByCityId(Long cityId);
}
