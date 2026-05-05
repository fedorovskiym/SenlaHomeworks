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
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private UUID id;
    @Column(name = "product_price_id")
    private UUID productPriceId;
    @Column(name = "product_id")
    private UUID productId;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "shop_branch_id")
    private UUID shopBranchId;
    @Column(name = "shop_name")
    private String shopName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private LocalUser user;

    public Subscription(UUID id, UUID productPriceId, UUID productId, String productName, UUID shopBranchId, String shopName, LocalUser user) {
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductPriceId() {
        return productPriceId;
    }

    public void setProductPriceId(UUID productPriceId) {
        this.productPriceId = productPriceId;
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
