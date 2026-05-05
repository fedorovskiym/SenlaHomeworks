package com.senla.ProductService.controller;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.service.PriceHistoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/history")
@Validated
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;
    private static final Logger logger = LoggerFactory.getLogger(PriceHistoryController.class);

    @Autowired
    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping(value = "/chart")
    public ResponseEntity<PriceHistoryDTO> getPriceHistory(@RequestParam UUID productId, @RequestParam UUID shopBranchId) {
        logger.info("Recieved request to get price history by product id {} and shop branch {} id /api/product-service/history", productId, shopBranchId);
        return ResponseEntity.status(HttpStatus.OK).body(priceHistoryService.getCoordsForChart(productId, shopBranchId));
    }

    @PostMapping(value = "/export")
    public ResponseEntity<?> exportPriceHistoryInTable(@Valid @RequestBody PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO) {
        logger.info("Recieved request to export price history over period of time information /api/product-service/history/export");
        String csv = priceHistoryService.generateCsv(periodOfTimeDTO);
        if (csv.isEmpty()) {
            logger.warn("Csv is empty, return NO_CONTENT /api/product-service/history/export");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No data found");
        }
        logger.info("Succesfull export price history over period of time information /api/product-service/history/export");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"price_history.csv\"")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(csv);
    }
}
