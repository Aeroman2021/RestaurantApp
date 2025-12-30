package com.restaurant.app.demo.service;


import com.restaurant.app.demo.model.dto.cart.CartRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface OrderService {
    OrderResponseDto checkOut(Long orderId,String idempotencyKey)throws Exception;
    OrderResponseDto upsertCart(CartRequestDto cartRequestDto,String idempotencyKey);
    OrderResponseDto updateOrder(OrderRequestDto orderRequestDto,Long orderId);
    OrderResponseDto updateStatus(Long orderId);
    void deleteById(Long orderId);
    OrderResponseDto getById(Long orderId);
    Page<OrderResponseDto> getAll(Pageable pageable);
    BigDecimal calculateFinalPrice(Order order);


}
