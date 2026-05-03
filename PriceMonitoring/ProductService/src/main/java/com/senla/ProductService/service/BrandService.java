package com.senla.ProductService.service;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.model.Brand;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    BrandDTO save(BrandDTO brandDTO, MultipartFile photo);

    List<BrandDTO> findAll();

    void delete(Long id);

    BrandDTO findById(Long id);

    Brand findByIdIfExists(Long id);

    Brand findByNameIfExists(String name);

    BrandDTO update(Long id, BrandUpdateDTO brandDTO);

    BrandDTO updateLogo(Long id, MultipartFile photo);

    Optional<Brand> findByIdOptional(Long id);
}
