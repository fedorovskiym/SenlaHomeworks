package com.senla.ProductService.controller;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.dto.ShopBranchUpdateDTO;
import com.senla.ProductService.service.ShopBranchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop_branch")
@Validated
public class ShopBranchController {

    private final ShopBranchService shopBranchService;
    private static final Logger logger = LoggerFactory.getLogger(ShopBranchController.class);

    @Autowired
    public ShopBranchController(ShopBranchService shopBranchService) {
        this.shopBranchService = shopBranchService;
    }

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ShopBranchDTO> createShopBranch(@Valid @RequestBody ShopBranchDTO shopBranchDTO) {
        logger.info("Received request to create shop branch /api/product-service/shop_branch/");
        ShopBranchDTO createdShopBranch = shopBranchService.save(shopBranchDTO);
        logger.info("Succesfull create shop branch /api/product-service/shop_branch/");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShopBranch);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ShopBranchDTO>> getShopBranchByCityId(@RequestParam UUID shopId) {
        logger.info("Recieved request to get shop branch by id product-service/shop_branch/{}", shopId);
        return ResponseEntity.status(HttpStatus.OK).body(shopBranchService.findAllByShopId(shopId));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ShopBranchDTO> getShopBranchById(@PathVariable UUID id) {
        logger.info("Recieved request to get shop branch by id product-service/shop_branch/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(shopBranchService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteShopBranch(@PathVariable UUID id) {
        logger.info("Recieved request to delete shop branch /api/product-service/shop_branch/{}", id);
        shopBranchService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ShopBranchDTO> updateShopBranch(
            @PathVariable UUID id,
            @RequestBody ShopBranchUpdateDTO shopBranchUpdateDTO) {
        logger.info("Recieved request to update shop branch /api/product-service/shop_branch/{}", id);
        ShopBranchDTO shopBranchDTO = shopBranchService.update(id, shopBranchUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(shopBranchDTO);
    }
}
