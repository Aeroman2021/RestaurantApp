package com.restaurant.app.demo.model.dto.order;

import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.entity.OrderItem;
import com.restaurant.app.demo.model.entity.enums.Status;

import java.util.List;

public record OrderRequestDto(
        Long userId,
        Status status,
        List<OrderItemRequestDto> orderItemList

) {

}
