package com.restaurant.app.demo.service;


import com.restaurant.app.demo.model.dto.OrderRequestDto;
import com.restaurant.app.demo.model.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto create(OrderRequestDto orderRequestDto);
    OrderResponseDto update(Long orderId,OrderRequestDto orderRequestDto);
    Page<OrderResponseDto> loadAll(Pageable pageable);
    OrderResponseDto getById(Long orderId);

}
