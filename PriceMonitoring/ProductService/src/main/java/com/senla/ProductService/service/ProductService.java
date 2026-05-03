package com.senla.ProductService.service;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductDTO save(ProductDTO productDTO, MultipartFile photo);

    List<ProductDTO> findAll();

    ProductDTO findById(Long id);

    Product findByIdIfExists(Long id);

    void delete(Long id);

    ProductDTO update(Long id, ProductUpdateDTO productUpdateDTO);

    ProductDTO updateImage(Long id, MultipartFile photo);

    void importFromCsv(MultipartFile file);

    Product findByName(String name);
}
