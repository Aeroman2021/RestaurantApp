package com.restaurant.app.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "restaurants", schema = "restaurant_application", indexes = {
        @Index(name = "idx_restaurant_lat_lng", columnList = "lat, lng"),
        @Index(name = "idx_restaurant_active", columnList = "is_active")
})
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 225)
    @NotNull
    @Column(name = "name", nullable = false, length = 225)
    private String name;

    @Size(max = 50)
    @Column(name = "phone", length = 50)
    private String phone;

    @Size(max = 500)
    @NotNull
    @Column(name = "address_text", nullable = false, length = 500)
    private String addressText;

    @Column(name = "lat", precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(name = "lng", precision = 10, scale = 7)
    private BigDecimal lng;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "restaurant")
    private Set<MenuItem> menueItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "restaurant")
    private Set<Order> orders = new LinkedHashSet<>();

    public Restaurant() {
    }

    public Restaurant(Long id, String name, String phone, String addressText, BigDecimal lat, BigDecimal lng,
                      Boolean isActive, Instant createdAt, Instant updatedAt, Set<MenuItem> menueItems, Set<Order> orders) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.addressText = addressText;
        this.lat = lat;
        this.lng = lng;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.menueItems = menueItems;
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Size(max = 225) @NotNull String getName() {
        return name;
    }

    public void setName(@Size(max = 225) @NotNull String name) {
        this.name = name;
    }

    public @Size(max = 50) String getPhone() {
        return phone;
    }

    public void setPhone(@Size(max = 50) String phone) {
        this.phone = phone;
    }

    public @Size(max = 500) @NotNull String getAddressText() {
        return addressText;
    }

    public void setAddressText(@Size(max = 500) @NotNull String addressText) {
        this.addressText = addressText;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public @NotNull Boolean getActive() {
        return isActive;
    }

    public void setActive(@NotNull Boolean active) {
        isActive = active;
    }

    public @NotNull Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotNull Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<MenuItem> getMenueItems() {
        return menueItems;
    }

    public void setMenueItems(Set<MenuItem> menueItems) {
        this.menueItems = menueItems;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}