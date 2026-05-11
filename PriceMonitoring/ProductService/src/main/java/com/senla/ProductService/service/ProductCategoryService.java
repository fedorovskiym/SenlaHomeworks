package com.senla.ProductService.service;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.model.ProductCategory;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * inteface for work with product categories
 */
public interface ProductCategoryService {

    /**
     * method for saving product category
     *
     * @param productCategoryDTO contains product category data
     * @param photo contains product category image
     * @return productCategoryDTO mapped from product
     * @throws EntityExistsException if product category with name from data already exists
     */
    ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO, MultipartFile photo);

    /**
     * method for finding all categories in system
     *
     * @return list of productCategoryDTO mapped from list productCategory
     */
    List<ProductCategoryDTO> findAll();

    /**
     * method for finding productCategoryDTO by id
     *
     * @param id from request to find product category
     * @return productCategoryDTO mapped from productCategory
     */
    ProductCategoryDTO findById(UUID id);

    /**
     * method for finding productCategory by id
     *
     * @param id from request to find product category
     * @return productCategory with id from request
     * @throws EntityNotFoundException if product category with id from request not found
     */
    ProductCategory findByIdIfExists(UUID id);

    /**
     * method for finding productCategory by name
     *
     * @param name from request to find product category
     * @return productCategory with name from request
     * @throws EntityNotFoundException if product category with name from request not found
     */
    ProductCategory findByNameIfExists(String name);

    /**
     * method for deleting product category by id
     *
     * @param id from request to delete product category
     */
    void deleteById(UUID id);

    /**
     * method for updating product category data
     *
     * @param id product category id to update
     * @param productCategoryUpdateDTO contains product category data
     * @return productCategoryDTO mapped from updated product category
     * @throws EntityExistsException if product category with new name exists or if new name = null
     */
    ProductCategoryDTO update(UUID id, ProductCategoryUpdateDTO productCategoryUpdateDTO);

    /**
     * method for update product category image
     *
     * @param id product category id to update
     * @param photo contains image
     * @return productCategoryDTO mapped from updated product category
     */
    ProductCategoryDTO updateImage(UUID id, MultipartFile photo);

    /**
     * method for finding all categoris with set ids
     *
     * @param setProductCategoryId set with categories ids
     * @return map with uuid and product categories
     */
    Map<UUID, ProductCategory> findAllById(Set<UUID> setProductCategoryId);
}
