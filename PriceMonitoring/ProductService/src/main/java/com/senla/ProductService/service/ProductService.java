package com.senla.ProductService.service;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.model.Product;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * interface for work with products
 */
public interface ProductService {

    /**
     * method for saving product from request
     *
     * @param productDTO contains product data
     * @param photo contains product image
     * @return productDTO mapped from saved product
     */
    ProductDTO save(ProductDTO productDTO, MultipartFile photo);

    /**
     * method for finding all products with pagination and filters
     *
     * @param productSearchDTO contains data for finding products
     * @return list productDTO mapped from list product
     */
    List<ProductDTO> findAll(ProductSearchDTO productSearchDTO);

    /**
     * method for finding productDTO by id
     *
     * @param id from request to find productDTO
     * @return productDTo mapped from product
     */
    ProductDTO findById(UUID id);

    /**
     * method for finding product by id
     *
     * @param id from request to find product
     * @return product with id from reuqest
     * @throws EntityNotFoundException if product with id from request not found
     */
    Product findByIdIfExists(UUID id);

    /**
     * method for deleting product by id
     *
     * @param id from request to delete product
     */
    void delete(UUID id);

    /**
     * method for updating product data by id
     *
     * @param id from request to update product
     * @param productUpdateDTO contains data to update product
     * @return productDTO mapped from updated product
     */
    ProductDTO update(UUID id, ProductUpdateDTO productUpdateDTO);

    /**
     * method for update product image by id
     *
     * @param id from request to update product image
     * @param photo contains image to update product
     * @return productDTO mapped from updated product
     */
    ProductDTO updateImage(UUID id, MultipartFile photo);

    /**
     * method for import products data from csv file
     *
     * @param file contains product data
     */
    void importFromCsv(MultipartFile file);

    /**
     * method for finding product by name
     *
     * @param name from reuqest to find product by name
     * @return product with name from request
     */
    Product findByName(String name);

    /**
     * method for find all products by ids
     *
     * @param setProductId contains set ids for finding products
     * @return map with uuid and products
     */
    Map<UUID, Product> findAllById(Set<UUID> setProductId);

    /**
     * method for finding product by id or null
     *
     * @param id from request to find product
     * @return entity if product with id exists or null if not
     */
    Product findByIdOrNull(UUID id);
}
