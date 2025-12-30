package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.dto.cart.CartRequestDto;
import com.restaurant.app.demo.model.dto.menuItem.MenuResponseDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.entity.*;
import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.*;
import com.restaurant.app.demo.service.OrderService;
import com.restaurant.app.demo.service.PricingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;
    private final CustomerLevelEvaluator customerLevelEvaluator;
    private final PricingStrategyFactory pricingStrategyFactory;
    private final ScoreService scoreService;
    private final RestaurantRepository restaurantRepository;

    public OrderServiceImpl(OrderRepository orderRepository,UserRepository userRepository,
                            MenuItemRepository menuItemRepository,RoleRepository roleRepository,
                            StringRedisTemplate redisTemplate, CustomerLevelEvaluator customerLevelEvaluator,
                            PricingStrategyFactory pricingStrategyFactory,RestaurantRepository restaurantRepository,
    ScoreService scoreService ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.roleRepository = roleRepository;
        this.redisTemplate= redisTemplate;
        this.customerLevelEvaluator = customerLevelEvaluator;
        this.pricingStrategyFactory = pricingStrategyFactory;
        this.scoreService= scoreService;
        this.restaurantRepository=restaurantRepository;
    }


    public OrderResponseDto upsertCart(CartRequestDto cartRequestDto,String idempotencyKey){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String redisKey = "order created: " + user.getId() + " : " + idempotencyKey;
        String cached = redisTemplate.opsForValue().get(redisKey);

        if(cached != null && !cached.equals("PROCESSING")){
            return showOrderDetails(Long.valueOf(cached));
        }

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(locked))
            throw new IllegalStateException("Order is being processed");

        try{
            Restaurant restaurant=restaurantRepository.findById(cartRequestDto.restaurantId())
                    .orElseThrow(()->new RuntimeException("Restaurant not found"));

            Order cart = orderRepository.findByUserAndStatus(user, Status.CART)
                    .orElseGet(() -> createCartForUser(user));

            if (cart.getRestaurant() != null &&
                    !cart.getRestaurant().getId().equals(restaurant.getId())) {
                throw new IllegalStateException("Cart already belongs to another restaurant");
            }

            Set<Long> menuItemSet = cartRequestDto.orderItemList()
                    .stream()
                    .map(OrderItemRequestDto::menuItem)
                    .collect(Collectors.toSet());
            long foundMenuCount = menuItemRepository.countByIdsAndRestaurantId(menuItemSet, restaurant.getId());
            if(foundMenuCount != menuItemSet.size())
                throw new RuntimeException("order item does not exist in the restaurant menu.");

            List<OrderItem> orderItems = setOrderItemsToAnOrderByorderItemList(cartRequestDto.orderItemList(), cart);
            cart.setOrderItems(orderItems);
            BigDecimal finalPrice = calculateFinalPrice(cart);
            cart.setTotalPrice(finalPrice);
            cart.setRestaurant(restaurant);

            Order savedCart = orderRepository.save(cart);
            redisTemplate.opsForValue().set(
                    redisKey,
                    savedCart.getId().toString(),
                    Duration.ofMinutes(10)
            );
            return showOrderDetails(savedCart.getId());
        }catch (Exception e){
            redisTemplate.delete(redisKey);
            throw e;
        }
    }

    Order createCartForUser(User user){
        Order cart = new Order();
        cart.setUser(user);
        cart.setStatus(Status.CART);
        return cart;
    }

    @Override
    public OrderResponseDto checkOut(Long orderId,String idempotencyKey) throws Exception {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean roleIsCustomer = authorities.stream().anyMatch(r -> r.getAuthority().equals("ROLE_CUSTOMER"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User foundedUser = userRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));

        Order existOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("order not found"));
        User user = userRepository.findById(existOrder.getUser().getId()).orElseThrow(()-> new RuntimeException("User not found"));

        if(!foundedUser.getId().equals(user.getId()))
            throw new RuntimeException("The cart is not belong to the current user");

        String redisKey = "order created: " + user.getId() + " : " + idempotencyKey;
        String cached = redisTemplate.opsForValue().get(redisKey);

        if(cached != null && !cached.equals("PROCESSING")){
            return showOrderDetails(Long.valueOf(cached));
        }

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(locked)) {
            throw new IllegalStateException("Order is being processed");
        }

        try{

            if(!existOrder.getStatus().equals(Status.CART))
                throw new RuntimeException("Invalid Order Status");

            existOrder.setOrderNumber(UUID.randomUUID().toString());
            existOrder.setCreatedAt(LocalDateTime.now());

            if (roleIsCustomer) {
                existOrder.setStatus(existOrder.getStatus().next(roleRepository.findByName("ROLE_CUSTOMER").get()));
            }

            CustomerLevel level = customerLevelEvaluator.evaluate(user.getTotalScore());
            PricingStrategy pricingStrategy = pricingStrategyFactory.getStrategy(level);
            BigDecimal finalPrice = pricingStrategy.calculateFinalPrice(existOrder);
            existOrder.setTotalPrice(finalPrice);

            Order savedOrder = orderRepository.save(existOrder);

            scoreService.addScore(user,savedOrder);

            redisTemplate.opsForValue().set(
                    redisKey,
                    savedOrder.getId().toString(),
                    Duration.ofHours(24)
            );

            return showOrderDetails(savedOrder.getId());
        }catch (Exception e){
            redisTemplate.delete(redisKey);
            throw e;
        }
    }

    public BigDecimal calculateFinalPrice(Order order) {
        return  order.getOrderItems()
                .stream()
                .map(req->{
                    MenuItem menuItem = menuItemRepository.findById(req.getMenuItem().getId()).orElseThrow(
                            ()->new RuntimeException("Menu Not Found"));
                    return menuItemRepository.findById(menuItem.getId()).get().getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
                }).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    private List<OrderItem> setOrderItemsToAnOrder(OrderRequestDto orderRequestDto, Order order) {
        return orderRequestDto.orderItemList()
                .stream()
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

    private List<OrderItem> setOrderItemsToAnOrderByorderItemList(List<OrderItemRequestDto> orderItemList, Order order) {
        return orderItemList
                .stream()
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

            CustomerLevel level = customerLevelEvaluator.evaluate(order.getUser().getTotalScore());
            PricingStrategy pricingStrategy = pricingStrategyFactory.getStrategy(level);
            BigDecimal finalPrice = pricingStrategy.calculateFinalPrice(order);

            order.getOrderItems().clear();
            order.getOrderItems().addAll(orderItems);
                    order.setTotalPrice(finalPrice);
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
                                .map(oi -> new MenuResponseDto(oi.getMenuItem().getName(),
                                        oi.getQuantity(),
                                        oi.getMenuItem().getPrice())).toList(),
                        o.getOrderItems().stream()
                                .map(mi -> mi.getMenuItem().getPrice().multiply(BigDecimal.valueOf(mi.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    }
}
