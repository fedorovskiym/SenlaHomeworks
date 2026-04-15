package com.senla.ProductService.controller;

import com.senla.ProductService.annotation.CheckId;
import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.service.ShopService;
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
@RequestMapping("/shop")
@Validated
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createShop(@Valid @RequestPart("shopDTO") ShopDTO shopDTO, @RequestPart("photo") MultipartFile photo) {
        shopService.save(shopDTO, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<ShopDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ShopDTO> findShopById(@CheckId @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShop(@CheckId @PathVariable Long id) {
        shopService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
