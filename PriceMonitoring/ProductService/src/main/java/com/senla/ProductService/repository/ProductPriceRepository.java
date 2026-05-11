package com.senla.ProductService.repository;

import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.enums.PriceStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductPriceRepository extends GenericRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO);

    List<ProductPrice> findProductInShops(UUID productId, UUID cityId);

    Optional<ProductPrice> findByProductIdAndShopBranchId(UUID productId, UUID shopBranchId);

    void saveList(List<ProductPrice> saveList);

    void updateList(List<ProductPrice> updateList);

    List<ProductPrice> findByUserQuery(UUID cityId, String productName, String categoryName,
                                       String brandName, String description);

    Optional<ProductPrice> findByIdWithFetch(UUID id);

    Map<UUID, ProductPrice> findAllById(Set<UUID> listProductPriceId);

    ProductPrice findByProductIdAndShopBranchIdAndStatus(UUID productId, UUID shopBranchId);
}
