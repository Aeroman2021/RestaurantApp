package com.restaurant.app.demo.model.dto;

import com.restaurant.app.demo.model.entity.enums.Status;

import java.util.List;

public record OrderResponseDto(
        Long orderId,
        Status orderStatus,
        String OrderId
) {
}
