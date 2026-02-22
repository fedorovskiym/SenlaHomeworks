package com.senla.task1.controller;

import com.senla.task1.dto.MechanicDTO;
import com.senla.task1.dto.MechanicDTORequest;
import com.senla.task1.exceptions.EntityAlreadyExistsException;
import com.senla.task1.exceptions.EntityNotFoundException;
import com.senla.task1.models.Mechanic;
import com.senla.task1.service.MechanicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mechanic")
public class MechanicController {

    private final MechanicService mechanicService;

    @Autowired
    public MechanicController(MechanicService mechanicService) {
        this.mechanicService = mechanicService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<HttpStatus> addMechanic(@RequestBody MechanicDTORequest mechanicDTORequest) {
        mechanicService.addMechanic(mechanicDTORequest.name(), mechanicDTORequest.surname(), mechanicDTORequest.experienceYears());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<MechanicDTO>> findAllMechanic() {
        return ResponseEntity.status(HttpStatus.OK).body(mechanicService.findAllMechanicDTO());
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HttpStatus> removeMechanicById(@PathVariable("id") Integer id) {
        mechanicService.removeMechanicById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/sort/alphabet")
    public ResponseEntity<List<MechanicDTO>> showSortedMechanicByAlphabet(@RequestParam("flag") boolean flag) {
        return ResponseEntity.status(HttpStatus.OK).body(mechanicService.showSortedMechanicByAlphabet(flag));
    }

    @GetMapping(value = "/sort/busy")
    public ResponseEntity<List<MechanicDTO>> showSortedMechanicByBusy() {
        return ResponseEntity.status(HttpStatus.OK).body(mechanicService.showSortedMechanicByBusy());
    }

    @PostMapping(value = "/import")
    public ResponseEntity<HttpStatus> importMechanicFromCSV(@RequestParam("path") String filePath) {
        mechanicService.importFromCSV(filePath);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/export")
    public ResponseEntity<?> exportMechanicToCSV() {
        String csv = mechanicService.exportToCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mechanic.csv\"")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(csv);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExists(EntityAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public String handleIllegalArgument(IllegalArgumentException exception) {
        return "Wrong argument: " + exception.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
