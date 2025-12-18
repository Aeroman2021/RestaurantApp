package com.restaurant.app.demo.controller;


import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponseDto> create(@RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto result = orderService.create(orderRequestDto);
        return ApiResponse.ok(result,"Order created successfully");
    }

    @PutMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> update(@PathVariable Long orderId, @RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto result = orderService.update(orderId,orderRequestDto);
        return ApiResponse.ok(result,"Order created successfully");
    }


    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> getById(@PathVariable Long orderId){
        OrderResponseDto result = orderService.getById(orderId);
        return ApiResponse.ok(result,"Order fetched successfully");
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> deleteById(@PathVariable Long orderId){
        orderService.deleteById(orderId);
        return ApiResponse.ok(null,"Order created successfully");
    }

    @GetMapping
    public ApiResponse<Page<OrderResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<OrderResponseDto> result = orderService.loadAll(PageRequest.of(page, size));
        return ApiResponse.ok(result,"Orders fetched successfully");
    }


}
