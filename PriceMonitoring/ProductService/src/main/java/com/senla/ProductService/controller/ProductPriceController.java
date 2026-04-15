package com.senla.ProductService.controller;

import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.service.ProductPriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/price")
@Validated
public class ProductPriceController {

    private final ProductPriceService productPriceService;

    public ProductPriceController(ProductPriceService productPriceService) {
        this.productPriceService = productPriceService;
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createProductPrice(@Valid @RequestBody CreateProductPriceDTO createProductPriceDTO) {
        productPriceService.save(createProductPriceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductPriceDTO> findById(
            @Min(value = 1, message = "Id must be greater than 0") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findById(id));
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductPriceDTO>> findAllWithPagination(
            @Min(value = 1, message = "Page number must be greater than 0") @RequestParam Integer page,
            @Min(value = 1, message = "Size number must be greater than 0") @RequestParam Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findAllWithPagination(page, size));
    }
}
