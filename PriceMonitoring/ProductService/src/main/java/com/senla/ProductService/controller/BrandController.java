package com.senla.ProductService.controller;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.service.BrandService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/brand")
@Validated
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> save(@Valid @RequestPart("brandDTO") BrandDTO brandDTO, @RequestPart("photo") MultipartFile photo) {
        brandService.save(brandDTO, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<BrandDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BrandDTO> findById(@Min(1) @NotNull @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteBrandById(@Min(1) @NotNull @PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateBrand(@Min(1) @NotNull @PathVariable Long id, @Valid @RequestBody BrandUpdateDTO brandDTO) {
        brandService.update(id, brandDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping(value = "/{id}/logo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateBrandLogo(@Min(1) @NotNull @PathVariable Long id, @RequestPart("photo") MultipartFile photo) {
        brandService.updateLogo(id, photo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
