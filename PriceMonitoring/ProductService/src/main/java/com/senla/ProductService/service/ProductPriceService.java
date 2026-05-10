package com.senla.ProductService.service;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.PriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.price.UpdateProductPrice;
import com.senla.ProductService.dto.subscription.SubscriptionDetailsDTO;
import com.senla.ProductService.model.ProductPrice;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductPriceService {

    ProductPriceDTO save(CreateUpdateProductPriceDTO createProductPriceDTO);

    ProductPriceDTO findById(UUID id);

    void delete(UUID id);

    List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO);

    ComparePrice comparePricesInShops(UUID productId, UUID cityId);

    List<PriceDTO> buildOtherPrices(List<ProductPrice> productPrices);

    List<ProductPrice> findProductInShops(UUID productId, UUID cityId);

    void importFromCsv(MultipartFile file);

    ProductPrice findByProductIdAndShopBranchId(UUID productId, UUID shopBranchId);

    List<ProductPriceDTO> search(UUID cityId, String searchQuery);

    ProductPrice findByIdIfExists(UUID id);

    ProductPriceDTO update(UUID id, CreateUpdateProductPriceDTO createProductPriceDTO);

    void subscribe(UUID id);

    void createRequest(UUID id, UpdateProductPrice updateProductPrice);

    ProductPriceDTO acceptRequest(UUID id, String status);

    SubscriptionDetailsDTO findSubscriptionByIdWithDetails(UUID id);
}
