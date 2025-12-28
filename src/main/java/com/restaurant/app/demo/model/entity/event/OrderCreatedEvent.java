package com.restaurant.app.demo.model.entity.event;

import com.restaurant.app.demo.model.entity.Order;

public class OrderCreatedEvent {
    private final Order order;

    public OrderCreatedEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
