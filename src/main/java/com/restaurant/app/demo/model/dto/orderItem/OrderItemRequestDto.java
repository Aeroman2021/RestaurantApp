package com.restaurant.app.demo.model.dto.orderItem;

public record OrderItemRequestDto(
        Long menuItem,
        int quantity
) {
}
