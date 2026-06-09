package com.senla.ProductService.service;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.model.Brand;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * interface for work with brands
 */
public interface BrandService {

    /**
     * method for save brand from request
     *
     * @param brandDTO contains brand's data
     * @param photo contains brand's logo image
     * @return brandDTO mapped from saved brand
     * @throws EntityExistsException if brand with name from request exists
     */
    BrandDTO save(BrandDTO brandDTO, MultipartFile photo);

    /**
     * method for finding all brands in system
     *
     * @return list of brandDTO
     */
    List<BrandDTO> findAll();

    /**
     * method for delete brand
     *
     * @param id from request
     */
    void delete(UUID id);

    /**
     * method for find brandDTO from db
     *
     * @param id from request
     * @return brandDTO mapped from brand
     */
    BrandDTO findById(UUID id);

    /**
     * method for find brand and throw exception if brand not found
     *
     * @param id from request
     * @return brand
     * @throws EntityNotFoundException if brand not found
     */
    Brand findByIdIfExists(UUID id);

    /**
     * method for find brandh by name and return null if brand not found
     *
     * @param name from request
     * @return brand
     */
    Brand findByNameIfExists(String name);

    /**
     * method for update brand withoit logo image
     *
     * @param id from request
     * @param brandDTO (name, country)
     * @return brandDTO from updated brand
     * @throws EntityExistsException if brand with name from request already exists
     */
    BrandDTO update(UUID id, BrandUpdateDTO brandDTO);

    /**
     * method for update brand's logo image
     *
     * @param id from request
     * @param photo new logo
     * @return brandDTO from updated brana
     */
    BrandDTO updateLogo(UUID id, MultipartFile photo);

    /**
     * method for find all brands with set ids
     *
     * @param setBrandId set with brands ids
     * @return map with uuid and brands
     */
    Map<UUID, Brand> findAllById(Set<UUID> setBrandId);
}
