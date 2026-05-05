package com.senla.ProductService.controller;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
@Validated
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestPart ProductDTO productDTO, @RequestPart MultipartFile photo) {
        logger.info("Recieved request to create product /api/product-service/product/");
        ProductDTO createdProduct = productService.save(productDTO, photo);
        logger.info("Succesfull create product /api/product-service/product/");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductDTO>> findAll() {
        logger.info("Recieved request to find all /api/product-service/product/");
        //TODO: очень ббудет здорово если добавишь пагинацию через спецификацию, всё тянуть из бд будет не очень правильно
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable UUID id) {
        logger.info("Recieved request to find product /api/product-service/product/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable UUID id) {
        logger.info("Recieved request to delete product /api/product-service/product/{}", id);
        productService.delete(id);
        logger.info("Succesfull delete product /api/product-service/product/{}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestBody ProductUpdateDTO productUpdateDTO) {
        logger.info("Recieved request to update product /api/product-service/product/{}", id);
        ProductDTO updatedProduct = productService.update(id, productUpdateDTO);
        logger.info("Succesfull update product /api/product-service/product/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @PatchMapping(value = "/{id}/logo")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable UUID id, @RequestPart MultipartFile photo) {
        logger.info("Recieved request to update product image by id /api/product-service/product/{}", id);
        ProductDTO updatedProduct = productService.updateImage(id, photo);
        logger.info("Succesfull update product image by id /api/product-service/product/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @PostMapping(value = "/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> importProduct(@RequestPart("file") MultipartFile file) {
        logger.info("Recieved request to import products from file /api/product-service/product/import");
        productService.importFromCsv(file);
        logger.info("Succesfull import products from file /api/product-service/product/import");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
