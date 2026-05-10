package com.senla.ProductService.service;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.model.ProductCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductCategoryService {

    ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO, MultipartFile photo);

    List<ProductCategoryDTO> findAll();

    ProductCategoryDTO findById(UUID id);

    ProductCategory findByIdIfExists(UUID id);

    ProductCategory findByNameIfExists(String name);

    void deleteById(UUID id);

    ProductCategoryDTO update(UUID id, ProductCategoryUpdateDTO productCategoryUpdateDTO);

    ProductCategoryDTO updateImage(UUID id, MultipartFile photo);

    Optional<ProductCategory> findByIdOptional(UUID id);

    Map<UUID, ProductCategory> findAllById(Set<UUID> setProductCategoryId);
}
