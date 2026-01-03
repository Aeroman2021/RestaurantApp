package com.restaurant.app.demo.service;


import com.restaurant.app.demo.model.dto.cart.CartRequestDto;
import com.restaurant.app.demo.model.dto.order.CheckoutOrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.dto.order.OrderStatusResponseDto;
import com.restaurant.app.demo.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface OrderService {
    OrderResponseDto checkOut(CheckoutOrderRequestDto checkoutOrderRequestDto,Long id)throws Exception;
    OrderResponseDto upsertCart(CartRequestDto cartRequestDto,String idempotencyKey);
    OrderResponseDto updateOrder(OrderRequestDto orderRequestDto,Long orderId);
    OrderStatusResponseDto updateStatus(Long orderId);
    OrderStatusResponseDto cancelTheOrder(Long orderId);
    void deleteById(Long orderId);
    OrderResponseDto getById(Long orderId);
    Page<OrderResponseDto> getAll(Pageable pageable);
    BigDecimal calculateFinalPrice(Order order);
}
