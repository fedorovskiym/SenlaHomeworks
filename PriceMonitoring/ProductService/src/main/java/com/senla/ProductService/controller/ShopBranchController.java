package com.senla.ProductService.controller;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.service.ShopBranchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shop_branch")
@Validated
public class ShopBranchController {

    private final ShopBranchService shopBranchService;

    @Autowired
    public ShopBranchController(ShopBranchService shopBranchService) {
        this.shopBranchService = shopBranchService;
    }


    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createShopBranch(@Valid @RequestBody ShopBranchDTO shopBranchDTO) {
        shopBranchService.save(shopBranchDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ShopBranchDTO>> getShopBranchByCityId(@Min(1) @RequestParam Long shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(shopBranchService.findAllByShopId(shopId));
    }
}
