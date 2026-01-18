package com.senla.task1.models.enums;

public enum OrderStatus {
    WAITING("Ожидает"),
    ACCEPTED("Принят"),
    DONE("Выполнен"),
    CANCEL("Отменен"),
    DELETED("Удален");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
