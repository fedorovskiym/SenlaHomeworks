package com.senla.task1.models.enums;

public enum MechanicSortType {
    ALPHABET("surname"),
    BUSY("is_busy");

    private final String displayName;


    MechanicSortType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
