package com.senla.ProductService.service;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ProductService {

    ProductDTO save(ProductDTO productDTO, MultipartFile photo);

    List<ProductDTO> findAll(ProductSearchDTO productSearchDTO);

    ProductDTO findById(UUID id);

    Product findByIdIfExists(UUID id);

    void delete(UUID id);

    ProductDTO update(UUID id, ProductUpdateDTO productUpdateDTO);

    ProductDTO updateImage(UUID id, MultipartFile photo);

    void importFromCsv(MultipartFile file);

    Product findByName(String name);

    Map<UUID, Product> findAllById(Set<UUID> listProductId);
}
