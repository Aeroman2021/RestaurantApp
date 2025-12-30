package com.restaurant.app.demo.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "order_items")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "menue_item_id")
    @JsonIgnore
    private MenuItem menuItem;

    @Column(name = "price_at_order")
    private BigDecimal priceAtOrder;


    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

}
