package com.restaurant.app.demo.model.dto.cart;

import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;

import java.util.List;

public record CartRequestDto(
        Long userId,
        Long orderId,
        List<OrderItemRequestDto> orderItemList
) {
}
