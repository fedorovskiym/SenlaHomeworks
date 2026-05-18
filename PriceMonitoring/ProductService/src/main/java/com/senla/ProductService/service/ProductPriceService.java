package com.senla.ProductService.service;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.PriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.price.UpdateProductPrice;
import com.senla.ProductService.dto.subscription.SubscriptionDetailsDTO;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.enums.PriceStatus;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * interaface for work with product price
 */
public interface ProductPriceService {

    /**
     * method for saving product price
     *
     * @param createProductPriceDTO contains product price data
     * @return productPriceDTO mapped from saved product price
     * @throws EntityExistsException if product price with product id and shop branch id from data already exists
     */
    ProductPriceDTO save(CreateUpdateProductPriceDTO createProductPriceDTO);

    /**
     * method for finging productPriceDTO by id
     *
     * @param id from request fo find productPriceDTO
     * @return productPriceDTO mapped from productPrice
     */
    ProductPriceDTO findById(UUID id);

    /**
     * method for deleting product price by id
     *
     * @param id from request to delete product price
     */
    void delete(UUID id);

    /**
     * method for find product prices with pagination
     *
     * @param productPriceSearchDTO contains data for finding product prices
     * @return list productPriceDTO mapped from list productPrice
     * @throws InvalidParameterException if sort type from productPriceSearchDTO invalid or invalid status from productPriceSearchDTO
     */
    List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO);

    /**
     * method for comparind prices on product in different shops
     *
     * @param productId product id which compares
     * @param cityId for finding prices on product in city
     * @return ComparePrice
     * @throws EntityNotFoundException if product prices on product in city not exists
     */
    ComparePrice comparePricesInShops(UUID productId, UUID cityId);

    /**
     * method for importing prices from csv file
     *
     * @param file containd data with product prices
     */
    void importFromCsv(MultipartFile file);

    /**
     * method for finding product price by product id and shop branch id or null
     *
     * @param productId contains product is to find product price
     * @param shopBranchId contains shop branch id to find product price
     * @return product price with product id and shop branch id
     */
    ProductPrice findByProductIdAndShopBranchId(UUID productId, UUID shopBranchId);

    /**
     * method for finding product prices from user search request
     *
     * @param cityId contains city id for finding product price in city
     * @param searchQuery contains data for finding product prices from user request
     * @return list productPriceDTO mapped from list productPrice
     */
    List<ProductPriceDTO> search(UUID cityId, String searchQuery);

    /**
     * method for finding product price by id
     *
     * @param id from request to finding product price
     * @return productPrice
     * @throws EntityNotFoundException if product price with id from request not found
     */
    ProductPrice findByIdIfExists(UUID id);

    /**
     * method for updating product price
     *
     * @param id from request to update product price
     * @param updateProductPrice contains data for updating product price
     * @return productPriceDTO mapped from updated productPrice
     */
    ProductPriceDTO update(UUID id, UpdateProductPrice updateProductPrice);

    /**
     * method for subscribing on product price
     *
     * @param id product price id from request to subscribe
     * @throws EntityNotFoundException if product price with id from request not found
     */
    void subscribe(UUID id);

    /**
     * method for creating change price request by casual users
     *
     * @param id from request to create request on changing product price
     * @param updateProductPrice contains data for creating request
     */
    void createRequest(UUID id, UpdateProductPrice updateProductPrice);

    /**
     * method for accepting change product price request by admins
     *
     * @param id from request to accept changes in product price
     * @param status new product price status from request
     * @return productPriceDTO mapped from updated product price
     */
    ProductPriceDTO acceptRequest(UUID id, PriceStatus status);

    /**
     * method for finding sibscription details by id
     *
     * @param id from request to find subscription details
     * @return subscriptionDetailsDTO with subscription data
     */
    SubscriptionDetailsDTO findSubscriptionByIdWithDetails(UUID id);

    /**
     * method for finding product in different shops in city
     *
     * @param productId contains product id
     * @param cityId contains city id
     * @return list product price with product id and city id from request
     */
    List<ProductPrice> findProductInShops(UUID productId, UUID cityId);

    /**
     * method for finding product price by id or null
     *
     * @param id contains product price id
     * @return product price with id or null
     */
    ProductPrice findByIdOrNull(UUID id);

    /**
     * method for building product price from request
     *
     * @param id product price id
     * @param updateProductPrice contains data for building product price
     * @return built product price from updateProductPrice with id
     */
    ProductPrice buildProductPriceFromRequest(UUID id, UpdateProductPrice updateProductPrice);
}
