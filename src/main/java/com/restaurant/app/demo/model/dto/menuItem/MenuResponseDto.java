package com.restaurant.app.demo.model.dto.menuItem;

import java.math.BigDecimal;

public record MenuResponseDto(
        String name,
        int quantity,
        BigDecimal price
) {
}
