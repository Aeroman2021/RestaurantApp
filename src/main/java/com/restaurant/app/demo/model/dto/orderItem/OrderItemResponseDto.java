package com.restaurant.app.demo.model.dto.orderItem;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long orderId,
        Long menuItem,
        int quantity,
        BigDecimal priceAtOrder

) {
}
