package com.senla.ProductService.controller;

import com.senla.ProductService.annotation.CheckId;
import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.service.ProductCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/category")
@Validated
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductCategoryDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.findAll());
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createCategory(
            @Valid @RequestPart("productCategoryDTO") ProductCategoryDTO productCategoryDTO,
            @RequestPart("photo") MultipartFile photo) {
        productCategoryService.save(productCategoryDTO, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductCategoryDTO> findById(@CheckId  @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productCategoryService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCategoryById(@CheckId @PathVariable Long id) {
        productCategoryService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateCategory(@CheckId @PathVariable Long id, @RequestBody ProductCategoryUpdateDTO productCategoryUpdateDTO) {
        productCategoryService.update(id, productCategoryUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping(value = "/{id}/logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateCategoryImage(@CheckId @PathVariable Long id, @RequestPart("photo") MultipartFile photo) {
        productCategoryService.updateImage(id, photo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
