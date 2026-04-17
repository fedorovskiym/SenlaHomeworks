package com.senla.ProductService.service;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    void save(ProductDTO productDTO, MultipartFile photo);

    List<ProductDTO> findAll();

    List<ProductDTO> findByCategoryId(Long categoryId);

    List<ProductDTO> findByBrandId(Long brandId);

    ProductDTO findById(Long id);

    Product findByIdIfExists(Long id);

    void delete(Long id);

    void update(Long id, ProductUpdateDTO productUpdateDTO);

    void updateImage(Long id, MultipartFile photo);

    Product findWithPrices(Long id);
}
