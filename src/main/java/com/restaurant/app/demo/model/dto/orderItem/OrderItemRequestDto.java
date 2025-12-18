package com.restaurant.app.demo.model.dto.orderItem;

import java.math.BigDecimal;

public record OrderItemRequestDto(
        int menuItem,
        int quantity,
        BigDecimal priceAtOrder

) {
}
