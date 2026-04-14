package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.model.ShopBranch;

import java.util.List;

public interface ShopBranchService {

    void save(ShopBranchDTO shopBranchDTO);

    List<ShopBranchDTO> findAllByCityId(Long cityId);

    ShopBranchDTO findById(Long id);

    ShopBranch findByIdIfExists(Long id);
}
