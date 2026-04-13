package com.senla.ProductService.service;

import com.senla.ProductService.dto.ProductCategoryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductCategoryService {

    void save(ProductCategoryDTO productCategoryDTO, MultipartFile photo);

    List<ProductCategoryDTO> findAllWithPagination(Integer page, Integer size);

    ProductCategoryDTO findById(Long id);

    void deleteById(Long id);
}
