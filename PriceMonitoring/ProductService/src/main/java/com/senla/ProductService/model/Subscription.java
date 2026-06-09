package com.senla.ProductService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @Column(name = "id")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_price_id")
    private ProductPrice productPrice;
    @Column(name = "user_id")
    private UUID userId;

    public Subscription(UUID id, ProductPrice productPrice, UUID userId) {
        this.id = id;
        this.productPrice = productPrice;
        this.userId = userId;
    }

    public Subscription() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProductPrice getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(ProductPrice productPrice) {
        this.productPrice = productPrice;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
                ", productPriceId=" + productPrice.getId() +
                ", productId=" + productPrice.getProduct().getId() +
                ", productName=" + productPrice.getProduct().getName() +
                ", shopBranchId=" + productPrice.getShopBranch().getId() +
                ", shopName=" + productPrice.getShopBranch().getShop().getName() +
                ", userId=" + userId +
                '}';
    }
}
