package com.restaurant.app.demo.controller;


import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<OrderResponseDto> create(@RequestBody OrderRequestDto orderRequestDto,
                                                @RequestHeader("Idempotency-Key") String idempotencyKey) throws Exception {
        OrderResponseDto result = orderService.create(orderRequestDto,idempotencyKey);
        return ApiResponse.ok(result,"Order created successfully");
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<OrderResponseDto> updateOrder(@PathVariable Long orderId, @RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto result = orderService.updateOrder(orderRequestDto,orderId);
        return ApiResponse.ok(result,"Order created successfully");
    }

    @PutMapping("admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponseDto> updateStatus(@PathVariable Long orderId){
        OrderResponseDto result = orderService.updateStatus(orderId);
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

        Page<OrderResponseDto> result = orderService.getAll(PageRequest.of(page, size,dir,property));
        return ApiResponse.ok(result,"Orders fetched successfully");
    }

}
