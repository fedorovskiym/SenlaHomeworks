package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.dto.ShopBranchUpdateDTO;
import com.senla.ProductService.model.ShopBranch;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * interface for work with shop branches
 */
public interface ShopBranchService {

    /**
     * method for saving shop branch
     *
     * @param shopBranchDTO contains shop branch data
     * @return shopBranchDTO mapped from saved shopBranch
     */
    ShopBranchDTO save(ShopBranchDTO shopBranchDTO);

    /**
     * method for finding all shopBranches by shopId
     *
     * @param shopId from request to find shop branches by shopId
     * @return list shopBranchDTO mapped from list shopBranch with shopId from request
     */
    List<ShopBranchDTO> findAllByCityIdAndShopId(UUID cityId, UUID shopId);

    /**
     * method for finding shopBranchDTO by id
     *
     * @param id from request to find shopBranchDTO
     * @return shopBranchDTO mapped from shopBranch
     */
    ShopBranchDTO findById(UUID id);

    /**
     * method for finding shopBranch by id
     *
     * @param id from request to find shopBranch
     * @return shopBranch by id from request
     * @throws EntityNotFoundException if shopBranch with id from request not found
     */
    ShopBranch findByIdIfExists(UUID id);

    /**
     * method for finding shop branches by set ids
     *
     * @param listShopBranchId contains set ids
     * @return map with uuid and shop branches
     */
    Map<UUID, ShopBranch> findAllById(Set<UUID> listShopBranchId);

    /**
     * method for deleting shop branch by id
     *
     * @param id from request to delete shop branch
     */
    void delete(UUID id);

    /**
     * method for updating shop branch
     *
     * @param id shop branch id to update
     * @param shopBranchUpdateDTO contains data
     * @return shopBranchDTO mapped from updated shopBranch
     */
    ShopBranchDTO update(UUID id, ShopBranchUpdateDTO shopBranchUpdateDTO);
}
