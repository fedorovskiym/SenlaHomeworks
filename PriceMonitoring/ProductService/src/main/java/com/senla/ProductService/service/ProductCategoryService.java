package com.senla.ProductService.service;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.model.ProductCategory;
import jakarta.validation.constraints.Min;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductCategoryService {

    void save(ProductCategoryDTO productCategoryDTO, MultipartFile photo);

    List<ProductCategoryDTO> findAll();

    ProductCategoryDTO findById(Long id);

    ProductCategory findByIdIfExists(Long id);

    ProductCategory findByNameIfExists(String name);

    void deleteById(Long id);

    void update(Long id, ProductCategoryUpdateDTO productCategoryUpdateDTO);

    void updateImage(Long id, MultipartFile photo);
}
