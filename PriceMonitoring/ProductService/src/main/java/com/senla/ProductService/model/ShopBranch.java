package com.senla.ProductService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "shop_branches")
public class ShopBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @Column(name = "street")
    private String street;
    @Column(name = "house")
    private Integer house;
    @Column(name = "room")
    private Integer room;
    @OneToMany(mappedBy = "shopBranch")
    private List<ProductPrice> productPrices;

    public ShopBranch(Long id, Shop shop, City city, String street, Integer house, Integer room) {
        this.id = id;
        this.shop = shop;
        this.city = city;
        this.street = street;
        this.house = house;
        this.room = room;
    }

    public ShopBranch() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public List<ProductPrice> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(List<ProductPrice> productPrices) {
        this.productPrices = productPrices;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShopBranch that = (ShopBranch) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ShopBranch{" +
                "id=" + id +
                ", shop=" + shop.getName() +
                ", city=" + city.getName() +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", room=" + room +
                '}';
    }
}
