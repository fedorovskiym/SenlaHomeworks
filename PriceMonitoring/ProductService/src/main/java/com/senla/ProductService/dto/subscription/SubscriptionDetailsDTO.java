package com.senla.ProductService.dto.subscription;

import com.senla.ProductService.dto.price.PriceDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SubscriptionDetailsDTO {

    private UUID id;
    private UUID productId;
    private String productName;
    private UUID shopBranchId;
    private String shopName;
    private String shopAddress;
    private Double price;
    private Integer discountPercent;
    private LocalDate startDate;
    private List<PriceDTO> otherPrices;

    public SubscriptionDetailsDTO(UUID id, UUID productId, String productName, UUID shopBranchId, String shopName,
                                  String shopAddress, Double price, Integer discountPercent,
                                  LocalDate startDate, List<PriceDTO> otherPrices) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.shopBranchId = shopBranchId;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.price = price;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.otherPrices = otherPrices;
    }

    public SubscriptionDetailsDTO() {
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getShopBranchId() {
        return shopBranchId;
    }

    public void setShopBranchId(UUID shopBranchId) {
        this.shopBranchId = shopBranchId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<PriceDTO> getOtherPrices() {
        return otherPrices;
    }

    public void setOtherPrices(List<PriceDTO> otherPrices) {
        this.otherPrices = otherPrices;
    }
}
