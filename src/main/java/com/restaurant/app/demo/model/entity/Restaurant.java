package com.restaurant.app.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "restaurants", schema = "restaurant_application", indexes = {
        @Index(name = "idx_restaurant_lat_lng", columnList = "lat, lng"),
        @Index(name = "idx_restaurant_active", columnList = "is_active")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "restaurant")
    private Set<MenuItem> menueItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "restaurant")
    private Set<Order> orders = new LinkedHashSet<>();

}