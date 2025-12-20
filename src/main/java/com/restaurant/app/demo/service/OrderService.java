package com.restaurant.app.demo.service;


import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto create(OrderRequestDto orderRequestDto);
    OrderResponseDto update(Long orderId,OrderRequestDto orderRequestDto);
    void deleteById(Long orderId);
    OrderResponseDto getById(Long orderId);
    Page<OrderResponseDto> loadAll(Pageable pageable);
    Page<OrderResponseDto> getAll(Pageable pageable);


}
