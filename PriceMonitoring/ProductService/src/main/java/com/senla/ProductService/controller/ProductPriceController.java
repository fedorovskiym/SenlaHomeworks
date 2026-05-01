package com.senla.ProductService.controller;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.service.ProductPriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ProductPriceController.class);

    public ProductPriceController(ProductPriceService productPriceService) {
        this.productPriceService = productPriceService;
    }

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createProductPrice(@Valid @RequestBody CreateUpdateProductPriceDTO createProductPriceDTO) {
        logger.info("Received request to create product price /api/product-service/price/");
        productPriceService.save(createProductPriceDTO);
        logger.info("Succesfull create product price /api/product-service/price/");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductPriceDTO> findById(@Min(1) @PathVariable Long id) {
        logger.info("Received request to find product price /api/product-service/price/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findById(id));
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ProductPriceDTO>> findAllWithPagination(@Valid @RequestBody ProductPriceSearchDTO productPriceSearchDTO) {
        logger.info("Received request to find all product price with pagination /api/product-service/price/");
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findAllWithPagination(productPriceSearchDTO));
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProductPrice(@Min(1) @PathVariable Long id, @RequestBody CreateUpdateProductPriceDTO createUpdateProductPriceDTO) {
        logger.info("Received request to update product price by id /api/product-service/price/{}", id);
        productPriceService.update(id, createUpdateProductPriceDTO);
        logger.info("Succesfull update product price by id /api/product-service/price/{}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/compare")
    public ResponseEntity<ComparePrice> comparePricesInShop(@Min(1) @RequestParam("productId") Long productId, @Min(1) @RequestParam("cityId") Long cityId) {
        logger.info("Recieved request to compare prices in shops by productId {} and cityId {} /api/product-service/price/compare", productId, cityId);
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.comparePricesInShops(productId, cityId));
    }

    @PostMapping(value = "/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> importPrices(@RequestPart MultipartFile file) {
        logger.info("Received request to import prices from file /api/product-service/price/import");
        productPriceService.importFromCsv(file);
        logger.info("Succesfull import prices from file /api/product-service/price/");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ProductPriceDTO>> searchProducts(@Min(1) @RequestParam("cityId") Long cityId, @RequestParam("searchQuery") String searchQuery) {
        logger.info("Received request to search prices /api/product-service/price/search");
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.search(cityId, searchQuery));
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<?> subscribe(@PathVariable Long id) {
        logger.info("Received request to subscribe /product-service/price/{}", id);
        productPriceService.sendSubscribeMessage(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
