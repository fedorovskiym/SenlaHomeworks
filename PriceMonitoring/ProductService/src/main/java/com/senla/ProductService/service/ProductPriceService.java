package com.senla.ProductService.service;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.model.ProductPrice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductPriceService {

    ProductPriceDTO save(CreateUpdateProductPriceDTO createProductPriceDTO);

    ProductPriceDTO findById(Long id);

    List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO);

    ComparePrice comparePricesInShops(Long productId, Long cityId);

    void importFromCsv(MultipartFile file);

    ProductPrice findByProductIdAndShopBranchId(Long productId, Long shopBranchId);

    List<ProductPriceDTO> search(Long cityId, String searchQuery);

    ProductPrice findByIdIfExists(Long id);

    ProductPriceDTO update(Long id, CreateUpdateProductPriceDTO createProductPriceDTO);

    void sendSubscribeMessage(Long id);
}
