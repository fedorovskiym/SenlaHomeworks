package com.senla.task1.controller;

import com.senla.task1.models.Mechanic;
import com.senla.task1.service.MechanicService;

public class MechanicController {

    private final MechanicService mechanicService = MechanicService.getInstance();

    public void addMechanic(Mechanic mechanic) {
        mechanicService.addMechanic(mechanic);
    }

    public void removeMechanicById(int id) {
        mechanicService.removeMechanicById(id);
    }

    public void showAllMechanics() {
        mechanicService.showAllMechanic();
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
