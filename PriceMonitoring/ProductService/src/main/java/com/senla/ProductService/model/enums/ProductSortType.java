package com.senla.ProductService.model.enums;

public enum ProductSortType {
    ID("id"),
    AMOUNT("amount"),
    NAME("name");


    private final String displayName;

    ProductSortType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
