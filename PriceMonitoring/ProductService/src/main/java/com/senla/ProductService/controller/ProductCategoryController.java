package com.senla.ProductService.controller;

import com.senla.ProductService.dto.ProductCategoryDTO;
import com.senla.ProductService.service.ProductCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductCategoryDTO>> findAllWithPagination(
            @Min(value = 1, message = "Page must be greater than 0 or equal to 0") @RequestParam Integer page,
            @Min(value = 0, message = "Size must be greater than 0 or equal to 0") @RequestParam Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.findAllWithPagination(page, size));
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createCategory(
            @Valid @RequestPart("productCategoryDTO") ProductCategoryDTO productCategoryDTO,
            @RequestPart("photo") MultipartFile photo) {
        productCategoryService.save(productCategoryDTO, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductCategoryDTO> findById(
            @PathVariable @Min(value = 0, message = "Id must be greater than 0 or equal to 0") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCategoryById(
            @PathVariable @Min(value = 0, message = "Id must be greater than 0 or equal to 0") Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
