package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopBranchDTO;

import java.util.List;

public interface ShopBranchService {

    void save(ShopBranchDTO shopBranchDTO);

    List<ShopBranchDTO> findAllWithPagination(Long cityId);
}
