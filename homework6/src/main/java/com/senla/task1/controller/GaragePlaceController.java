package com.senla.task1.controller;

import com.senla.task1.dto.GaragePlaceDTO;
import com.senla.task1.service.GaragePlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/garage_place")
public class GaragePlaceController {

    private final GaragePlaceService garagePlaceService;

    @Autowired
    public GaragePlaceController(GaragePlaceService garagePlaceService) {
        this.garagePlaceService = garagePlaceService;
    }

    @GetMapping(value = "/free")
    public List<GaragePlaceDTO> showFreeGaragePlaces() {
        return garagePlaceService.findFreeGaragePlaces();
    }

    @PostMapping(value = "/")
    public ResponseEntity<HttpStatus> addGaragePlace(@RequestBody GaragePlaceDTO garagePlaceDTO) {
        garagePlaceService.addGaragePlace(garagePlaceDTO.placeNumber());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> removeGaragePlace(@PathVariable("id") Integer id) {
        garagePlaceService.removeGaragePlace(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/import")
    public ResponseEntity<HttpStatus> importGaragePlaceFromCSV(@RequestParam("fileName") String fileName) {
        garagePlaceService.importFromCSV(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/export")
    public ResponseEntity<?> exportGaragePlaceToCSV() {
        String csv = garagePlaceService.exportToCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"places.csv\"")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(csv);
    }
}
