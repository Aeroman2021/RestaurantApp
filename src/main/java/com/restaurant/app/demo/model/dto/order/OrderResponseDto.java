package com.restaurant.app.demo.model.dto.order;

import com.restaurant.app.demo.model.entity.enums.Status;

public record OrderResponseDto(
        Long orderId,
        Status orderStatus,
        String orderNumber
) {
}
