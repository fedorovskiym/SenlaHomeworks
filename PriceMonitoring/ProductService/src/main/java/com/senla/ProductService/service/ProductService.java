package com.senla.ProductService.service;

import com.senla.ProductService.dto.ProductDTO;
import com.senla.ProductService.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    void save(ProductDTO productDTO, MultipartFile photo);

    List<ProductDTO> findByCategoryId(Long categoryId);

    List<ProductDTO> findByBrandId(Long brandId);

    ProductDTO findById(Long id);

    Product findByIdIfExists(Long id);
}
