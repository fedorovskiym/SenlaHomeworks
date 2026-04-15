package com.senla.ProductService.service;

import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;

import java.util.List;

public interface ProductPriceService {

    void save(CreateProductPriceDTO createProductPriceDTO);

    ProductPriceDTO findById(Long id);

    List<ProductPriceDTO> findAllWithPagination(Integer page, Integer size);
}
