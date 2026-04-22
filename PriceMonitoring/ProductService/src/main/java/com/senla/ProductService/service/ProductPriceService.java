package com.senla.ProductService.service;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.model.ProductPrice;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductPriceService {

    void save(CreateProductPriceDTO createProductPriceDTO);

    ProductPriceDTO findById(Long id);

    List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO);

    ComparePrice comparePricesInShops(Long productId, Long cityId);

    void importFromCsv(MultipartFile file);

    ProductPrice findByProductIdAndShopBranchId(Long productId, Long shopBranchId);

    List<ProductPriceDTO> search(Long cityId, String searchQuery);
}
