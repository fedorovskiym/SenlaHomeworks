package com.senla.ProductService.controller;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.service.BrandService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brand")
@Validated
public class BrandController {

    private final BrandService brandService;
    private static final Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BrandDTO> save(
            @Valid @RequestPart("brandDTO") BrandDTO brandDTO,
            @RequestPart("photo") MultipartFile photo) {
        logger.info("Recieved request to save brand /api/product-service/brand/");
        BrandDTO brand = brandService.save(brandDTO, photo);
        logger.info("Succesfull save brand /api/product-service/brand/");
        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<BrandDTO>> findAll() {
        logger.info("Recieved request to find all brands /api/product-service/brand/");
        return ResponseEntity.status(HttpStatus.OK).body(brandService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BrandDTO> findById(@PathVariable UUID id) {
        logger.info("Recieved request to find brand by id /api/product-service/brand/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(brandService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deleteBrandById(@PathVariable UUID id) {
        logger.info("Recieved request to delete brand by id /api/product-service/brand/{}", id);
        brandService.delete(id);
        logger.info("Succesfull delete brand by id /api/product-service/brand/{}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BrandDTO> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody BrandUpdateDTO brandDTO) {
        logger.info("Recieved request to update brand with id /api/product-service/brand/{}", id);
        BrandDTO updateBrand = brandService.update(id, brandDTO);
        logger.info("Succesfull update brand with id /api/product-service/brand/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updateBrand);
    }

    @PatchMapping(value = "/{id}/logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BrandDTO> updateBrandLogo(
            @PathVariable UUID id,
            @RequestPart("photo") MultipartFile photo) {
        logger.info("Recieved request to update brand logo with id /api/product-service/brand/{}", id);
        BrandDTO updateBrand = brandService.updateLogo(id, photo);
        logger.info("Succesfull update brand logo with id /api/product-service/brand/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updateBrand);
    }
}
