package com.restaurant.app.demo.model.dto.order;

import com.restaurant.app.demo.model.entity.enums.Status;

import java.time.LocalDateTime;

public record OrderStatusResponseDto(
        Long orderId,
        Status orderStatus,
        LocalDateTime updatedAt
) {
}
