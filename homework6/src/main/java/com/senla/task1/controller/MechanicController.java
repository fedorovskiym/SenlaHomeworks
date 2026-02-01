package com.senla.task1.controller;

import com.senla.task1.models.Mechanic;
import com.senla.task1.service.MechanicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MechanicController {

    private final MechanicService mechanicService;

    @Autowired
    public MechanicController(MechanicService mechanicService) {
        this.mechanicService = mechanicService;
    }

    public void addMechanic(String name, String surname, double experienceYears) {
        mechanicService.addMechanic(name, surname, experienceYears);
    }

    public List<Mechanic> findAllMechanic() {
        return mechanicService.findAllMechanic();
    }

    public void removeMechanicById(int id) {
        mechanicService.removeMechanicById(id);
    }

    public void showAllMechanics() {
        mechanicService.showAllMechanic(findAllMechanic());
    }

    public void showSortedMechanicByAlphabet(boolean flag) {
        mechanicService.showSortedMechanicByAlphabet(flag);
    }

    public void showSortedMechanicByBusy() {
        mechanicService.showSortedMechanicByBusy();
    }

    public void importMechanicFromCSV(String filePath) {
        mechanicService.importFromCSV(filePath);
    }

    public void exportMechanicToCSV(String filePath) {
        mechanicService.exportToCSV(filePath);
    }
}
