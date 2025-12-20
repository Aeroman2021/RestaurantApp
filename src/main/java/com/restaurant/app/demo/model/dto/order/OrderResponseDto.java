package com.restaurant.app.demo.model.dto.order;

import com.restaurant.app.demo.model.dto.menuItem.MenuResponseDto;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.enums.Status;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDto(
        Long orderId,
        Status orderStatus,
        String orderNumber,
        List<MenuResponseDto> menuResponseDtos,
        BigDecimal totalPrice
) {
}
