package com.senla.ProductService.dto.price;

public class PriceDTO {

    private Double price;
    private String shopName;
    private String shopAddress;
    private String shopLogoImageUrl;

    public PriceDTO(Double price, String shopName, String shopAddress, String shopLogoImageUrl) {
        this.price = price;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopLogoImageUrl = shopLogoImageUrl;
    }

    public PriceDTO() {

    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public String getShopLogoImageUrl() {
        return shopLogoImageUrl;
    }

    public void setShopLogoImageUrl(String shopLogoImageUrl) {
        this.shopLogoImageUrl = shopLogoImageUrl;
    }
}
