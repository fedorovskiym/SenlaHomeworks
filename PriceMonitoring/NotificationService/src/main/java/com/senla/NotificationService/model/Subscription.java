package com.senla.NotificationService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;
    @Column(name = "product_price_id")
    private Long productPriceId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "shop_branch_id")
    private Long shopBranchId;
    @Column(name = "shop_name")
    private String shopName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private LocalUser user;

    public Subscription(Long id, Long productPriceId, Long productId, String productName, Long shopBranchId, String shopName, LocalUser user) {
        this.id = id;
        this.productPriceId = productPriceId;
        this.productId = productId;
        this.productName = productName;
        this.shopBranchId = shopBranchId;
        this.shopName = shopName;
        this.user = user;
    }

    public Subscription() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductPriceId() {
        return productPriceId;
    }

    public void setProductPriceId(Long productPriceId) {
        this.productPriceId = productPriceId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getShopBranchId() {
        return shopBranchId;
    }

    public void setShopBranchId(Long shopBranchId) {
        this.shopBranchId = shopBranchId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public LocalUser getUser() {
        return user;
    }

    public void setUser(LocalUser user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", productPriceId=" + productPriceId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", shopBranchId=" + shopBranchId +
                ", shopName='" + shopName + '\'' +
                ", user=" + user +
                '}';
    }
}
