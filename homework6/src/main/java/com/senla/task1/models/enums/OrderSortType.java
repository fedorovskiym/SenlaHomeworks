package com.senla.task1.models.enums;

public enum OrderSortType {
    DATE_OF_SUBMISSION("submissionDateTime"),
    DATE_OF_COMPLETION("completionDateTime"),
    PRICE("price");

    private final String displayName;

    OrderSortType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
