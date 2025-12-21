package com.restaurant.app.demo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.app.demo.model.dto.menuItem.MenuResponseDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.OrderItem;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.repository.OrderRepository;
import com.restaurant.app.demo.repository.RoleRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(OrderRepository orderRepository,UserRepository userRepository,
                            MenuItemRepository menuItemRepository,RoleRepository roleRepository,
                            StringRedisTemplate redisTemplate,ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.roleRepository = roleRepository;
        this.redisTemplate= redisTemplate;
        this.objectMapper=objectMapper;
    }

    private User findUserById(Long uerId){
        return userRepository.findById(uerId).orElseThrow(
                ()-> new RuntimeException("User not found")
        );
    }

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto,String idempotencyKey) throws Exception {

        String redisKey = "order created: " + orderRequestDto.userId() + " : " + idempotencyKey;
        String cached = redisTemplate.opsForValue().get(redisKey);

        if(cached != null && !cached.equals("PROCESSING")){
            return showOrderDetails(Long.valueOf(cached));
        }

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(locked)) {
            throw new IllegalStateException("Order is being processed");
        }

        try{
            Order order = new Order();
            List<OrderItem> orderItems = setOrderItemsToAnOrder(orderRequestDto, order);
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
            Order result = orderRepository.save(order);

            redisTemplate.opsForValue().set(
                    redisKey,
                    result.getId().toString(),
                    Duration.ofHours(24)
            );
            return showOrderDetails(result.getId());
        }catch (Exception e){
            redisTemplate.delete(redisKey);
            throw e;
        }

    }

    private List<OrderItem> setOrderItemsToAnOrder(OrderRequestDto orderRequestDto, Order order) {
        return orderRequestDto.orderItemList().stream()
                .map(reqDto ->
                {
                    MenuItem menuItem = menuItemRepository.findById(reqDto.menuItem()).orElseThrow(
                            () -> new RuntimeException("Menu Not Found")
                    );
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setMenuItem(menuItem);
                    item.setQuantity(reqDto.quantity());
                    item.setPriceAtOrder(menuItem.getPrice());
                    return item;
                }).toList();
    }

    @Override
    public OrderResponseDto updateOrder(OrderRequestDto orderRequestDto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("order Not found"));
        if(orderRequestDto.orderItemList() != null){
            List<OrderItem> orderItems = setOrderItemsToAnOrder(orderRequestDto, order);
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

        Order result = orderRepository.save(order);
        return showOrderDetails(result.getId());
    }

    @Override
    public OrderResponseDto updateStatus(Long orderId) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean roleIsAdmin = authorities.stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("order Not found"));

        if (roleIsAdmin && order.getStatus() != null) {
            order.setStatus(order.getStatus().next(roleRepository.findByName("ADMIN").get()));
        }

        Order result = orderRepository.save(order);
        return showOrderDetails(result.getId());
    }

    public OrderResponseDto showOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));

        List<MenuResponseDto> menuItems = order.getOrderItems().stream()
                .map(oi -> new MenuResponseDto(oi.getMenuItem().getName(), oi.getQuantity(), oi.getMenuItem().getPrice())).toList();

        BigDecimal totalPrice = menuItems.stream()
                .map(mi -> mi.price().multiply(BigDecimal.valueOf(mi.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponseDto(order.getId(), order.getStatus(), order.getOrderNumber(), menuItems, totalPrice);
    }


    @Override
    public OrderResponseDto getById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order Not found"));
        return showOrderDetails(order.getId());
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
                        o.getOrderNumber(),
                        o.getOrderItems().stream()
                                .map(oi -> new MenuResponseDto(oi.getMenuItem().getName(), oi.getQuantity(), oi.getMenuItem().getPrice())).toList(),
                        o.getOrderItems().stream()
                                .map(mi -> mi.getMenuItem().getPrice().multiply(BigDecimal.valueOf(mi.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    }
}
