package com.senla.task1.models.enums;

public enum OrderSortType {
    DATE_OF_SUBMISSION("submission_date_time"),
    DATE_OF_COMPLETION("completion_date_time"),
    PRICE("price");

    private final String displayName;

    OrderSortType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
