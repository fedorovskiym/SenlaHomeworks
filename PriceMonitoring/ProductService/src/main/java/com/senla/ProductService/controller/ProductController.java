package com.senla.ProductService.controller;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
@Validated
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProduct(@Valid @RequestPart ProductDTO productDTO, @RequestPart MultipartFile photo) {
        productService.save(productDTO, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@Min(1) @NotNull @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteById(@Min(1) @NotNull @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateProduct(@Min(1) @NotNull @PathVariable Long id, @RequestBody ProductUpdateDTO productUpdateDTO) {
        productService.update(id, productUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping(value = "/{id}/logo")
    public ResponseEntity<?> updateProductImage(@Min(1) @NotNull @PathVariable Long id, @RequestPart MultipartFile photo) {
        productService.updateImage(id, photo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/import")
    public ResponseEntity<?> importProduct(@RequestPart("file") MultipartFile file) {
        productService.importFromCsv(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
