package com.restaurant.app.demo.model.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Table(name = "order_items")
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "menue_item_id")
    private MenuItem menuItem;



    @Column(name = "price_at_order")
    private BigDecimal priceAtOrder;


    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(int quantity, MenuItem menuItem, BigDecimal priceAtOrder, Order order) {
        this.quantity = quantity;
        this.menuItem = menuItem;
        this.priceAtOrder = priceAtOrder;
        this.order = order;
    }

    public OrderItem(Long id, int quantity, MenuItem menuItem, BigDecimal priceAtOrder) {
        this.id = id;
        this.quantity = quantity;
        this.menuItem = menuItem;
        this.priceAtOrder = priceAtOrder;
    }

    public OrderItem(int quantity, MenuItem menuItem, BigDecimal priceAtOrder) {
        this.quantity = quantity;
        this.menuItem = menuItem;
        this.priceAtOrder = priceAtOrder;
    }

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
