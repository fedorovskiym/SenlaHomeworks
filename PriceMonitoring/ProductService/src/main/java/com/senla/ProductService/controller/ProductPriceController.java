package com.senla.ProductService.controller;

import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.price.UpdateProductPrice;
import com.senla.ProductService.model.enums.PriceStatus;
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
@RequestMapping("/price")
@Validated
public class ProductPriceController {

    private final ProductPriceService productPriceService;
    private static final Logger logger = LoggerFactory.getLogger(ProductPriceController.class);

    public ProductPriceController(ProductPriceService productPriceService) {
        this.productPriceService = productPriceService;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductPriceDTO> createProductPrice(
            @Valid @RequestBody CreateUpdateProductPriceDTO createProductPriceDTO) {
        logger.info("Received request to create product price /api/product-service/price/");
        ProductPriceDTO createdProductPrice = productPriceService.save(createProductPriceDTO);
        logger.info("Succesfull create product price /api/product-service/price/");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductPrice);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductPriceDTO> findById(@PathVariable UUID id) {
        logger.info("Received request to find product price /api/product-service/price/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findById(id));
    }

    @PostMapping(value = "/")
    public ResponseEntity<List<ProductPriceDTO>> findAllWithPagination(
            @Valid @RequestBody ProductPriceSearchDTO productPriceSearchDTO) {
        logger.info("Received request to find all product price with pagination /api/product-service/price/");
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.findAllWithPagination(productPriceSearchDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deleteProductPrice(@PathVariable UUID id) {
        logger.info("Received request to delete product price /api/product-service/price/{}", id);
        productPriceService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductPriceDTO> updateProductPrice(
            @PathVariable UUID id, @RequestBody UpdateProductPrice updateProductPrice) {
        logger.info("Received request to update product price by id /api/product-service/price/{}", id);
        ProductPriceDTO updatedProductPrice = productPriceService.update(id, updateProductPrice);
        logger.info("Succesfull update product price by id /api/product-service/price/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProductPrice);
    }

    @GetMapping(value = "/compare")
    public ResponseEntity<ComparePrice> comparePricesInShop(
            @RequestParam("productId") UUID productId, @RequestParam("cityId") UUID cityId) {
        logger.info("""
                Recieved request to compare prices in shops by productId {}
                and cityId {} /api/product-service/price/compare
                """, productId, cityId);
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.comparePricesInShops(productId, cityId));
    }

    @PostMapping(value = "/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> importPrices(@RequestPart MultipartFile file) {
        logger.info("Received request to import prices from file /api/product-service/price/import");
        productPriceService.importFromCsv(file);
        logger.info("Succesfull import prices from file /api/product-service/price/");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ProductPriceDTO>> searchProducts(
            @RequestParam("cityId") UUID cityId, @RequestParam("searchQuery") String searchQuery) {
        logger.info("Received request to search prices /api/product-service/price/search");
        return ResponseEntity.status(HttpStatus.OK).body(productPriceService.search(cityId, searchQuery));
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> subscribe(@PathVariable UUID id) {
        logger.info("Received request to subscribe /api/product-service/price/{}", id);
        productPriceService.subscribe(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/request")
    public ResponseEntity<HttpStatus> createRequestToChangePrice(
            @PathVariable UUID id, @Valid @RequestBody UpdateProductPrice updateProductPrice) {
        logger.info("Received request to change price /api/product-service/price/{}", id);
        productPriceService.createRequest(id, updateProductPrice);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/accept")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductPriceDTO> acceptRequestToChangePrice(
            @PathVariable UUID id,
            @RequestBody PriceStatus status) {
        logger.info("Received request to accept request /api/product-service/price/{}", id);
        ProductPriceDTO priceDTO = productPriceService.acceptRequest(id, status);
        return ResponseEntity.status(HttpStatus.OK).body(priceDTO);
    }
}
