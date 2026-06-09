package com.senla.ProductService.dto.price;

import java.util.List;
import java.util.UUID;

public class ComparePrice {
    private UUID productId;
    private String productName;
    private String productImageUrl;
    private Double minPrice;
    private String shopNameMin;
    private String shopLogoImageUrl;
    private String shopAddressMin;
    private List<PriceDTO> otherPrices;

    public ComparePrice(UUID productId, String productName, String productImageUrl,
                        Double minPrice, String shopNameMin, String shopLogoImageUrl,
                        String shopAddressMin, List<PriceDTO> otherPrices) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.minPrice = minPrice;
        this.shopNameMin = shopNameMin;
        this.shopLogoImageUrl = shopLogoImageUrl;
        this.shopAddressMin = shopAddressMin;
        this.otherPrices = otherPrices;
    }

    public ComparePrice() {
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

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public String getShopNameMin() {
        return shopNameMin;
    }

    public void setShopNameMin(String shopNameMin) {
        this.shopNameMin = shopNameMin;
    }

    public String getShopAddressMin() {
        return shopAddressMin;
    }

    public void setShopAddressMin(String shopAddressMin) {
        this.shopAddressMin = shopAddressMin;
    }

    public List<PriceDTO> getOtherPrices() {
        return otherPrices;
    }

    public void setOtherPrices(List<PriceDTO> otherPrices) {
        this.otherPrices = otherPrices;
    }

    public String getShopLogoImageUrl() {
        return shopLogoImageUrl;
    }

    public void setShopLogoImageUrl(String shopLogoImageUrl) {
        this.shopLogoImageUrl = shopLogoImageUrl;
    }
}
