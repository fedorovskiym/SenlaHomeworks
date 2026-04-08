package com.senla.ProductService.controller;

import com.senla.ProductService.dto.BrandDTO;
import com.senla.ProductService.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> save(@RequestPart("brandDTO") BrandDTO brandDTO, @RequestPart("photo") MultipartFile photo) {
        brandService.save(brandDTO, photo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<BrandDTO>> findAllWithPagination(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.findWithPagination(page, size));
    }
}
