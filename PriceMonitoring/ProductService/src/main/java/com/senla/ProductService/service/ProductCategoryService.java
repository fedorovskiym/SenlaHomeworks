package com.senla.ProductService.service;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.model.ProductCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryService {

    ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO, MultipartFile photo);

    List<ProductCategoryDTO> findAll();

    ProductCategoryDTO findById(Long id);

    ProductCategory findByIdIfExists(Long id);

    ProductCategory findByNameIfExists(String name);

    void deleteById(Long id);

    ProductCategoryDTO update(Long id, ProductCategoryUpdateDTO productCategoryUpdateDTO);

    ProductCategoryDTO updateImage(Long id, MultipartFile photo);

    Optional<ProductCategory> findByIdOptional(Long id);
}
