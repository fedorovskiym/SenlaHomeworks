package com.senla.ProductService.dto.price;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public class CreateUpdateProductPriceDTO {

    @NotNull(message = "Product's id must be not null")
    @CsvBindByName(column = "productId")
    private Long productId;
    @NotNull(message = "Shop branch's id must be not null")
    @CsvBindByName(column = "shopBranchId")
    private Long shopBranchId;
    @Min(value = 0, message = "Price must be positive")
    @CsvBindByName(column = "price")
    private Double price;
    @Range(min = 0, max = 100, message = "Discount must be between 0 and  100")
    @CsvBindByName(column = "discountPercent")
    private Integer discountPercent;


    public CreateUpdateProductPriceDTO() {
    }

    public CreateUpdateProductPriceDTO(Long productId, Long shopBranchId, Double price, Integer discountPercent) {
        this.productId = productId;
        this.shopBranchId = shopBranchId;
        this.price = price;
        this.discountPercent = discountPercent;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getShopBranchId() {
        return shopBranchId;
    }

    public void setShopBranchId(Long shopBranchId) {
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
        return "CreateProductPriceDTO{" +
                "productId=" + productId +
                ", shopBranchId=" + shopBranchId +
                ", price=" + price +
                ", discountPercent=" + discountPercent +
                '}';
    }
}
