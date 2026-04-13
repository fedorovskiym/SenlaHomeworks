package com.senla.ProductService.model.enums;

public enum PriceStatus {
    ON_REVIEW("На рассмотрении"),
    ACTUAL("актуальная"),
    OLD("старая");

    private String displayName;

    PriceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
