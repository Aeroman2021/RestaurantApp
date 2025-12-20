package com.restaurant.app.demo.service;


import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto create(OrderRequestDto orderRequestDto);
    OrderResponseDto updateOrder(OrderRequestDto orderRequestDto,Long orderId);
    OrderResponseDto updateStatus(Long orderId);
    void deleteById(Long orderId);
    OrderResponseDto getById(Long orderId);
    Page<OrderResponseDto> getAll(Pageable pageable);


}
