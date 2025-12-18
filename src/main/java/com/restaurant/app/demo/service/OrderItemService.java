package com.restaurant.app.demo.service;

import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.dto.orderItem.OrderItemResponseDto;

public interface OrderItemService {
    OrderItemResponseDto create(OrderItemRequestDto orderItemRequestDto);

}
