package com.senla.ProductService.dto.price;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

public class CreateUpdateProductPriceDTO {

    @CsvBindByName(column = "id")
    private UUID id;
    @NotNull(message = "Product's id must be not null")
    @CsvBindByName(column = "productId")
    private UUID productId;
    @NotNull(message = "Shop branch's id must be not null")
    @CsvBindByName(column = "shopBranchId")
    private UUID shopBranchId;
    @Min(value = 0, message = "Price must be positive")
    @CsvBindByName(column = "price")
    private Double price;
    @Range(min = 0, max = 100, message = "Discount must be between 0 and  100")
    @CsvBindByName(column = "discountPercent")
    private Integer discountPercent;


    public CreateUpdateProductPriceDTO() {
    }

    public CreateUpdateProductPriceDTO(UUID id, UUID productId, UUID shopBranchId,
                                       Double price, Integer discountPercent) {
        this.id = id;
        this.productId = productId;
        this.shopBranchId = shopBranchId;
        this.price = price;
        this.discountPercent = discountPercent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getShopBranchId() {
        return shopBranchId;
    }

    public void setShopBranchId(UUID shopBranchId) {
        this.shopBranchId = shopBranchId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return "CreateUpdateProductPriceDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", shopBranchId=" + shopBranchId +
                ", price=" + price +
                ", discountPercent=" + discountPercent +
                '}';
    }
}
