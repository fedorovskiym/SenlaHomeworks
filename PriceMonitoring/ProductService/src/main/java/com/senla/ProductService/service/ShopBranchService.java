package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.model.ShopBranch;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShopBranchService {

    ShopBranchDTO save(ShopBranchDTO shopBranchDTO);

    List<ShopBranchDTO> findAllByShopId(UUID shopId);

    ShopBranchDTO findById(UUID id);

    ShopBranch findByIdIfExists(UUID id);

    Map<UUID, ShopBranch> findAllById(Set<UUID> listShopBranchId);
}
