package com.senla.ProductService.controller;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.service.ProductPriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/price")
@Validated
public class ProductPriceController {

    private final ProductPriceService productPriceService;

    public ProductPriceController(ProductPriceService productPriceService) {
        this.productPriceService = productPriceService;
    }

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createProductPrice(@Valid @RequestBody CreateUpdateProductPriceDTO createProductPriceDTO) {
        productPriceService.save(createProductPriceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductPriceDTO> findById(@Min(1) @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findById(id));
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductPriceDTO>> findAllWithPagination(@Valid @RequestBody ProductPriceSearchDTO productPriceSearchDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findAllWithPagination(productPriceSearchDTO));
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProductPrice(@Min(1) @PathVariable Long id, @RequestBody CreateUpdateProductPriceDTO createUpdateProductPriceDTO) {
        productPriceService.update(id, createUpdateProductPriceDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/compare")
    public ResponseEntity<ComparePrice> comparePricesInShop(@Min(1) @RequestParam("productId") Long productId, @Min(1) @RequestParam("cityId") Long cityId) {
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.comparePricesInShops(productId, cityId));
    }

    @PostMapping(value = "/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> importPrices(@RequestPart MultipartFile file) {
        productPriceService.importFromCsv(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ProductPriceDTO>> searchProducts(@Min(1) @RequestParam("cityId") Long cityId, @RequestParam("searchQuery") String searchQuery) {
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.search(cityId, searchQuery));
    }
}
