package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.OrderRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User findUserById(Long uerId){
        return userRepository.findById(uerId).orElseThrow(
                ()-> new RuntimeException("User not found")
        );
    }

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderItems(orderRequestDto.orderItemList());
        order.setStatus(Status.CREATED);
        order.setUser(findUserById(orderRequestDto.userId()));
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder.getId(),savedOrder.getStatus(),savedOrder.getOrderNumber());
    }

    @Override
    public OrderResponseDto update(Long orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));
        order.setOrderItems(orderRequestDto.orderItemList());
        order.setStatus(orderRequestDto.status());
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder.getId(),savedOrder.getStatus(),savedOrder.getOrderNumber());
    }

    @Override
    public Page<OrderResponseDto> loadAll(Pageable pageable) {
        return (Page<OrderResponseDto>) orderRepository.findAll(pageable)
                .stream()
                .map(order->new OrderResponseDto(order.getId(),order.getStatus(),order.getOrderNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto getById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));
        return new OrderResponseDto(order.getId(),order.getStatus(),order.getOrderNumber());
    }
}
