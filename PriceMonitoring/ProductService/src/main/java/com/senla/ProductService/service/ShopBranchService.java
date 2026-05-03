package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.model.ShopBranch;

import java.util.List;

public interface ShopBranchService {

    ShopBranchDTO save(ShopBranchDTO shopBranchDTO);

    List<ShopBranchDTO> findAllByShopId(Long shopId);

    ShopBranchDTO findById(Long id);

    ShopBranch findByIdIfExists(Long id);
}
