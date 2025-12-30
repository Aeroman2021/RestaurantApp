package com.restaurant.app.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.app.demo.model.entity.enums.FulfillmentType;
import com.restaurant.app.demo.model.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
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
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 50)
    @Column(name = "order_number", length = 50)
    private String orderNumber;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
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

    @ColumnDefault("'PICKUP'")
    @Column(name = "fulfillment_type")
    @Enumerated(EnumType.STRING)
    private FulfillmentType fulfillmentType;

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

    @CreatedDate
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "checked_out_at")
    private LocalDateTime checkedOutAt;

    @CreatedBy
    @Column(name = "created_by",updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

}