package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.OrderItem;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.repository.OrderItemRepository;
import com.restaurant.app.demo.repository.OrderRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, MenuItemRepository menuItemRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    private User findUserById(Long uerId){
        return userRepository.findById(uerId).orElseThrow(
                ()-> new RuntimeException("User not found")
        );
    }

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {

        Order order = new Order();
        List<OrderItem> orderItems = orderRequestDto.orderItemList().stream()
                .map(reqDto ->
                {
                    MenuItem menuItem = menuItemRepository.findById(reqDto.menuItem()).orElseThrow(
                            ()->new RuntimeException("Menu Not Found")
                    );
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setMenuItem(menuItem);
                    item.setQuantity(reqDto.quantity());
                    item.setPriceAtOrder(menuItem.getPrice());
                    return item;
                }).toList();

        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderItems(orderItems);
        order.setStatus(Status.CREATED);
        order.setUser(findUserById(orderRequestDto.userId()));
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalPrice = orderRequestDto.orderItemList().stream()
                .map(req->{
                    MenuItem menuItem = menuItemRepository.findById(req.menuItem()).orElseThrow(
                            ()->new RuntimeException("Menu Not Found"));
                    return menuItemRepository.findById(menuItem.getId()).get().getPrice().multiply(BigDecimal.valueOf(req.quantity()));
                 }).reduce(BigDecimal.ZERO,BigDecimal::add);

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder.getId(),savedOrder.getStatus(),savedOrder.getOrderNumber());
    }

    @Override
    public OrderResponseDto update(Long orderId, OrderRequestDto orderRequestDto) {

        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));

        boolean roleUser = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        boolean roleAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(roleUser && orderRequestDto.orderItemList() != null){
            List<OrderItem> orderItems = orderRequestDto.orderItemList().stream()
                    .map(reqDto ->
                    {
                        MenuItem menuItem = menuItemRepository.findById(reqDto.menuItem()).orElseThrow(
                                ()->new RuntimeException("Menu Not Found")
                        );
                        OrderItem item = new OrderItem();
                        item.setOrder(order);
                        item.setMenuItem(menuItem);
                        item.setQuantity(reqDto.quantity());
                        item.setPriceAtOrder(menuItem.getPrice());
                        return item;
                    }).toList();

            BigDecimal totalPrice = orderRequestDto.orderItemList().stream()
                    .map(req->{
                        MenuItem menuItem = menuItemRepository.findById(req.menuItem()).orElseThrow(
                                ()->new RuntimeException("Menu Not Found"));
                        return menuItemRepository.findById(menuItem.getId()).get().getPrice().multiply(BigDecimal.valueOf(req.quantity()));
                    }).reduce(BigDecimal.ZERO,BigDecimal::add);

            order.getOrderItems().clear();
            order.getOrderItems().addAll(orderItems);
                    order.setTotalPrice(totalPrice);
        }

        if(roleAdmin && orderRequestDto.status()!=null){
            order.setStatus(orderRequestDto.status());

        }

        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder.getId(),savedOrder.getStatus(),savedOrder.getOrderNumber());
    }

    @Override
    public Page<OrderResponseDto> loadAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(order->new OrderResponseDto(order.getId(),order.getStatus(),order.getOrderNumber()));
    }

    @Override
    public OrderResponseDto getById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));
        return new OrderResponseDto(order.getId(),order.getStatus(),order.getOrderNumber());
    }

    @Override
    public void deleteById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));
        orderRepository.delete(order);
    }

    @Override
    public Page<OrderResponseDto> getAll(Pageable pageable) {
        return orderRepository.getAll(pageable)
                .map(o->new OrderResponseDto(
                        o.getId(),
                        o.getStatus(),
                        o.getOrderNumber()
                ));
    }
}
