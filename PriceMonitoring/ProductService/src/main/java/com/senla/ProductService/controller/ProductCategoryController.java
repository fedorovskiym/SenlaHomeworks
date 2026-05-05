package com.senla.ProductService.controller;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.service.ProductCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@Validated
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryController.class);

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductCategoryDTO>> findAll() {
        logger.info("Recieved request to find all categories /api/product-service/category/");
        return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.findAll());
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCategory(
            @Valid @RequestPart("productCategoryDTO") ProductCategoryDTO productCategoryDTO,
            @RequestPart("photo") MultipartFile photo) {
        logger.info("Recieved request to create a new category /api/product-service/category/");
        ProductCategoryDTO createdCategory = productCategoryService.save(productCategoryDTO, photo);
        logger.info("Succesfull create a new category /api/product-service/category/");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductCategoryDTO> findById(@PathVariable UUID id) {
        logger.info("Recieved request to find product category by id /api/product-service/category/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteCategoryById(@PathVariable UUID id) {
        logger.info("Recieved request to delete category by id /api/product-service/category/{}", id);
        productCategoryService.deleteById(id);
        logger.info("Succesfull delete category by id /api/product-service/category/{}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductCategoryDTO> updateCategory(@PathVariable UUID id, @RequestBody ProductCategoryUpdateDTO productCategoryUpdateDTO) {
        logger.info("Recieved request to update category by id /api/product-service/category/{}", id);
        ProductCategoryDTO updatedCategory = productCategoryService.update(id, productCategoryUpdateDTO);
        logger.info("Succesfull update category by id /api/product-service/category/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
    }

    @PatchMapping(value = "/{id}/logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductCategoryDTO> updateCategoryImage(@PathVariable UUID id, @RequestPart("photo") MultipartFile photo) {
        logger.info("Recieved request to update category image by id /api/product-service/category/{}", id);
        ProductCategoryDTO updatedCategory = productCategoryService.updateImage(id, photo);
        logger.info("Succesfull update category image by id /api/product-service/category/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
    }
}
