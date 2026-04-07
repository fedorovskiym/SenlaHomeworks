package com.senla.ProductService.model;

import com.senla.ProductService.model.enums.PriceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "product_prices")
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @Column(name = "price")
    private Double price;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "discount_percent")
    private Integer discountPercent;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PriceStatus status;


    public ProductPrice(Long id, Product product, Shop shop, Double price, LocalDate startDate, Integer discountPercent, PriceStatus status) {
        this.id = id;
        this.product = product;
        this.shop = shop;
        this.price = price;
        this.startDate = startDate;
        this.discountPercent = discountPercent;
        this.status = status;
    }

    public ProductPrice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public PriceStatus getStatus() {
        return status;
    }

    public void setStatus(PriceStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductPrice that = (ProductPrice) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProductPrice{" +
                "id=" + id +
                ", product=" + product +
                ", shop=" + shop +
                ", price=" + price +
                ", startDate=" + startDate +
                ", discountPercent=" + discountPercent +
                ", status=" + status +
                '}';
    }
}
