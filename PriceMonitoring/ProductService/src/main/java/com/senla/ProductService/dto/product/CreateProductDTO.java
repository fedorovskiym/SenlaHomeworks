package com.senla.ProductService.dto.product;

import com.opencsv.bean.CsvBindByName;

import java.util.UUID;

public class CreateProductDTO {
    @CsvBindByName(column = "id")
    private UUID productId;
    @CsvBindByName(column = "name")
    private String name;
    @CsvBindByName(column = "description")
    private String description;
    @CsvBindByName(column = "amount")
    private Double amount;
    @CsvBindByName(column = "unit")
    private String unit;
    @CsvBindByName(column = "brandId")
    private UUID brandId;
    @CsvBindByName(column = "categoryId")
    private UUID categoryId;

    public CreateProductDTO() {
    }

    public CreateProductDTO(UUID productId, String name, String description,
                            Double amount, String unit,
                            UUID brandId, UUID categoryId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.brandId = brandId;
        this.categoryId = categoryId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "CreateProductDTO{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", unit='" + unit + '\'' +
                ", brandId=" + brandId +
                ", categoryId=" + categoryId +
                '}';
    }
}
