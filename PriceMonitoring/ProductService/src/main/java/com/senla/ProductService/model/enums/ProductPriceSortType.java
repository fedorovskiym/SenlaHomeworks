package com.senla.ProductService.model.enums;

public enum ProductPriceSortType {
    PRICE("price"),
    DISCOUNT_PERCENT("discountPercent");

    private String displayName;

    ProductPriceSortType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
