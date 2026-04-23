package com.senla.ProductService.controller;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.service.PriceHistoryService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@Validated
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @Autowired
    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping(value = "/chart")
    public ResponseEntity<PriceHistoryDTO> getPriceHistory(@Min(1) @RequestParam Long productId, @Min(1) @RequestParam Long shopBranchId) {
        return ResponseEntity.status(HttpStatus.OK).body(priceHistoryService.getCoordsForChart(productId, shopBranchId));
    }
}
