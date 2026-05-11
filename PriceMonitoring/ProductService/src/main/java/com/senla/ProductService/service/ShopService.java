package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.model.Shop;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * interface for work with shops
 */
public interface ShopService {

    /**
     * method for saving shop
     *
     * @param shopDTO contains shop data
     * @param photo contains shop logo image
     * @return shopDTO mapped from saved shop
     * @throws EntityExistsException if shop with name from shopDTO exists
     */
    ShopDTO save(ShopDTO shopDTO, MultipartFile photo);

    /**
     * method for finding all shops in city
     *
     * @param cityId from request to find shop in city
     * @return list shopDTO mapped from list shop in city
     */
    List<ShopDTO> findAllByCityId(UUID cityId);

    /**
     * method for deleting shop by id
     *
     * @param id from request to delete shop
     */
    void delete(UUID id);

    /**
     * method for finding shopDTO by id
     *
     * @param id from request to find shopDTO
     * @return shopDTO mapped from shop
     */
    ShopDTO findById(UUID id);

    /**
     * method for finding shop by id
     *
     * @param id from request to find id
     * @return shop with id from request
     * @throws EntityNotFoundException if shop with id from request not found
     */
    Shop findByIdIfExists(UUID id);
}
