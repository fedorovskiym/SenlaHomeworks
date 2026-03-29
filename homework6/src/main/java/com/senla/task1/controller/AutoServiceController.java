package com.senla.task1.controller;

import com.senla.task1.dto.AutoServiceRequestDTO;
import com.senla.task1.dto.OrderDTO;
import com.senla.task1.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autoservice")
public class AutoServiceController {

    private final AutoService autoService;

    @Autowired
    public AutoServiceController(AutoService autoService) {
        this.autoService = autoService;
    }

    @PostMapping(value = "/")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody AutoServiceRequestDTO autoServiceRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autoService.createOrder(autoServiceRequestDTO));
    }

    @GetMapping(value = "/available_slot")
    public ResponseEntity<?> getAvailableSlot(@RequestParam("year") Integer year, @RequestParam("month") Integer month,
                                              @RequestParam("day") Integer day) {
        return ResponseEntity.status(HttpStatus.OK).body(autoService.getAvailableSlot(year, month, day));
    }

    @GetMapping(value = "/export")
    public ResponseEntity<?> exportOrdersToCSV() {
        String csv = autoService.exportOrdersToCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"order.csv\"")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(csv);
    }

    @PostMapping(value = "/import")
    public ResponseEntity<HttpStatus> importOrdersFromCSV(@RequestParam("fileName") String fileName) {
        autoService.importFromCSV(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
