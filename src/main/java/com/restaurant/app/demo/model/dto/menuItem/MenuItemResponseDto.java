package com.restaurant.app.demo.model.dto.menuItem;

import java.math.BigDecimal;

public record MenuItemResponseDto(

        Long id,
        String name,
        BigDecimal price,
        boolean isActive

) {
}
