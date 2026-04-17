package com.senla.ProductService.service;

import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

public interface ProductPriceService {

    void save(CreateProductPriceDTO createProductPriceDTO);

    ProductPriceDTO findById(Long id);

    List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO);

    List<ProductPriceDTO> comparePricesInShops(Long productId, Long cityId);
}
