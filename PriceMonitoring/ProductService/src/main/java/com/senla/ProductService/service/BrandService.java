package com.senla.ProductService.service;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.model.Brand;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandService {

    BrandDTO save(BrandDTO brandDTO, MultipartFile photo);

    List<BrandDTO> findAll();

    void delete(UUID id);

    BrandDTO findById(UUID id);

    Brand findByIdIfExists(UUID id);

    Brand findByNameIfExists(String name);

    BrandDTO update(UUID id, BrandUpdateDTO brandDTO);

    BrandDTO updateLogo(UUID id, MultipartFile photo);

    Optional<Brand> findByIdOptional(UUID id);
}
