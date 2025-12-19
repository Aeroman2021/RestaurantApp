package com.restaurant.app.demo.controller;


import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.entity.OrderItem;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.service.OrderService;
import jakarta.persistence.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<OrderResponseDto> create(@RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto result = orderService.create(orderRequestDto);
        return ApiResponse.ok(result,"Order created successfully");
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ApiResponse<OrderResponseDto> update(@PathVariable Long orderId, @RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto result = orderService.update(orderId,orderRequestDto);
        return ApiResponse.ok(result,"Order created successfully");
    }


    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<OrderResponseDto> getById(@PathVariable Long orderId){
        OrderResponseDto result = orderService.getById(orderId);
        return ApiResponse.ok(result,"Order fetched successfully");
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<OrderResponseDto> deleteById(@PathVariable Long orderId){
        orderService.deleteById(orderId);
        return ApiResponse.ok(null,"Order created successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ApiResponse<Page<OrderResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "orderNumber") String property
    ){
        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        List<String> allowedProperties = List.of("orderNumber","totalPrice","createdAt","status");

        if(!allowedProperties.contains(property)){
            throw new IllegalArgumentException("Invalid sort property");
        }

        Page<OrderResponseDto> result = orderService.loadAll(PageRequest.of(page, size,dir,property));
        return ApiResponse.ok(result,"Orders fetched successfully");
    }

}
