package com.senla.ProductService.controller;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.service.ShopService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop")
@Validated
public class ShopController {

    private final ShopService shopService;
    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ShopDTO> createShop(
            @Valid @RequestPart("shopDTO") ShopDTO shopDTO,
            @RequestPart("photo") MultipartFile photo) {
        logger.info("Recieved request to create shop /api/product-service/shop/");
        ShopDTO createdShop = shopService.save(shopDTO, photo);
        logger.info("Succesfully uploaded shop /api/product-service/shop/");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShop);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ShopDTO>> findAllByCityId(@RequestParam UUID cityId) {
        logger.info("Recieved request to get all shops by cityId {} /api/product-service/shop/", cityId);
        return ResponseEntity.status(HttpStatus.OK).body(shopService.findAllByCityId(cityId));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ShopDTO> findShopById(@PathVariable UUID id) {
        logger.info("Recieved request to get shop by id /api/product-service/shop/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(shopService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteShop(@PathVariable UUID id) {
        logger.info("Recieved request to delete shop by id /api/product-service/shop/{}", id);
        shopService.delete(id);
        logger.info("Succesfully deleted shop by id /api/product-service/shop/{}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
