package com.senla.ProductService.service;

import com.senla.ProductService.dto.ProductCategoryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductCategoryService {

    void save(ProductCategoryDTO productCategoryDTO, MultipartFile photo);

    List<ProductCategoryDTO> findAll();

    ProductCategoryDTO findById(Long id);

    void deleteById(Long id);
}
