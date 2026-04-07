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

}
