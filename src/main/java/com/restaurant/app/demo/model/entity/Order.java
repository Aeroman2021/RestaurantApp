package com.restaurant.app.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.app.demo.model.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "orders", schema = "restaurant_application", indexes = {
        @Index(name = "order_user_idx", columnList = "user_id"),
        @Index(name = "order_status_idx", columnList = "status"),
        @Index(name = "order_restaurant_idx", columnList = "restaurant_id"),
        @Index(name = "order_created_at_idx", columnList = "created_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "order_number", columnNames = {"order_number"})
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 50)
    @NotNull
    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @OneToMany(mappedBy = "menuItem",fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @Lob
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ColumnDefault("'PICKUP'")
    @Lob
    @Column(name = "fulfillment_type")
    private String fulfillmentType;

    @ColumnDefault("0.00")
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @ColumnDefault("0.00")
    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Size(max = 500)
    @Column(name = "delivery_address_text", length = 500)
    private String deliveryAddressText;

    @Column(name = "delivery_lat", precision = 10, scale = 7)
    private BigDecimal deliveryLat;

    @Column(name = "delivery_lng", precision = 10, scale = 7)
    private BigDecimal deliveryLng;

    public Order() {
    }

    public Order(Long id, User user, String orderNumber, List<OrderItem> orderItems, Status status, BigDecimal totalPrice,
                 Restaurant restaurant, LocalDateTime createdAt, String fulfillmentType, BigDecimal deliveryFee,
                 BigDecimal distanceKm, String deliveryAddressText, BigDecimal deliveryLat, BigDecimal deliveryLng) {
        this.id = id;
        this.user = user;
        this.orderNumber = orderNumber;
        this.orderItems = orderItems;
        this.status = status;
        this.totalPrice = totalPrice;
        this.restaurant = restaurant;
        this.createdAt = createdAt;
        this.fulfillmentType = fulfillmentType;
        this.deliveryFee = deliveryFee;
        this.distanceKm = distanceKm;
        this.deliveryAddressText = deliveryAddressText;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @Size(max = 50) @NotNull String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(@Size(max = 50) @NotNull String orderNumber) {
        this.orderNumber = orderNumber;
    }



    public @NotNull BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(@NotNull BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public @NotNull Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(@NotNull Restaurant restaurant) {
        this.restaurant = restaurant;
    }



    public String getFulfillmentType() {
        return fulfillmentType;
    }

    public void setFulfillmentType(String fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public @Size(max = 500) String getDeliveryAddressText() {
        return deliveryAddressText;
    }

    public void setDeliveryAddressText(@Size(max = 500) String deliveryAddressText) {
        this.deliveryAddressText = deliveryAddressText;
    }

    public BigDecimal getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(BigDecimal deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public BigDecimal getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(BigDecimal deliveryLng) {
        this.deliveryLng = deliveryLng;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public @NotNull LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotNull LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}