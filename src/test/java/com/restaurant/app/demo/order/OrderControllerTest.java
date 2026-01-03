package com.restaurant.app.demo.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restaurant.app.demo.controller.ApiResponse;
import com.restaurant.app.demo.model.dto.cart.CartRequestDto;
import com.restaurant.app.demo.model.dto.order.CheckoutOrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.dto.order.OrderStatusResponseDto;
import com.restaurant.app.demo.model.dto.user.UserResponseDto;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.OrderRepository;
import com.restaurant.app.demo.service.impl.CustomerLevelEvaluator;
import com.restaurant.app.demo.service.impl.PricingStrategyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class OrderControllerTest extends BaseIntegrationTest {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PricingStrategyFactory pricingStrategyFactory;

    @Autowired
    private CustomerLevelEvaluator customerLevelEvaluator;



    @Test
    void upsertCart_successfully() throws Exception{
        UserResponseDto user = createUser();

        OrderRequestDto orderRequestDto = generateOrderItem(user);

        CartRequestDto cartRequestDto = new CartRequestDto(user.id(),
                10L,
                orderRequestDto.restaurantId(),
                orderRequestDto.orderItemList());

        String content = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/orders")
                        .header("Authorization",TEST_JWT_TOKEN)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequestDto))

        ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderResponseDto> response =
                objectMapper.readValue(
                        content,
                        new TypeReference<>() {}
                );
        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNotNull();
    }

    @Test
    void checkout_order_successfully() throws Exception {

        long orderId = 4L;
        CheckoutOrderRequestDto checkoutOrderRequestDto = new CheckoutOrderRequestDto(
                "DELIVERY",
                "خیابان شریعتی، خیابان حقوقی،پلاک 75، واحد 21",
                new BigDecimal("35.704414"),
                new BigDecimal("51.4328312")
        );

        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/orders/" + orderId + "/checkOut")
                                .header("Authorization",TEST_JWT_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkoutOrderRequestDto))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderResponseDto> response =
                objectMapper.readValue(
                        content,
                        new TypeReference<>() {}
                );

    // ASSERT
        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNotNull();
    }

    @Test
    void endToEnd_cart_then_checkout_delivery_should_persist_all_compute_total() throws Exception {
        UserResponseDto user = createUser();
        OrderRequestDto orderRequestDto = generateOrderItem(user);

        CartRequestDto cartRequestDto = new CartRequestDto(user.id(),
                10L,
                orderRequestDto.restaurantId(),
                orderRequestDto.orderItemList());

        String idempotencyKey = UUID.randomUUID().toString();
        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/orders")
                                .header("Authorization",TEST_JWT_TOKEN)
                                .header("Idempotency-Key", idempotencyKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartRequestDto))

                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderResponseDto> cartResponse = objectMapper.readValue(content, new TypeReference<>() {});
        assertThat(cartResponse.success()).isTrue();
        assertThat(cartResponse.data()).isNotNull();

        Long cartId = cartResponse.data().orderId();
        assertThat(cartId).isNotNull();

        Order cart = orderRepository.findWithUserAndOrderItemsAndMenuAndRestaurantById(cartId).orElseThrow();
        assertThat(cart.getStatus()).isEqualTo(Status.CART);
        assertThat(cart.getRestaurant()).isNotNull();
        assertThat(cart.getRestaurant().getId()).isEqualTo(orderRequestDto.restaurantId());

        assertThat(cart.getOrderItems()).hasSize(orderRequestDto.orderItemList().size());
        cart.getOrderItems().forEach(oi->{
            assertThat(oi.getPriceAtOrder()).isNotNull();
            assertThat(oi.getMenuItem()).isNotNull();
            assertThat(oi.getQuantity()).isGreaterThan(0);
            assertThat(oi.getMenuItem().getRestaurant().getId()).isEqualTo(orderRequestDto.restaurantId());
        });

        BigDecimal cartTotalPriceBeforeCheckout = cart.getTotalPrice();
        assertThat(cartTotalPriceBeforeCheckout).isNotNull();

        CheckoutOrderRequestDto checkoutDto = new CheckoutOrderRequestDto(
                "DELIVERY",
                "Tehran, Jordan ...",
                new BigDecimal("35.7900000"),
                new BigDecimal("51.4200000")
        );

        String checkOutContent = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/orders/" + cart.getId() + "/checkOut")
                                .header("Authorization",TEST_JWT_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkoutDto))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderResponseDto> checkOutResponse = objectMapper.readValue(checkOutContent, new TypeReference<>() {});
        assertThat(checkOutResponse.success()).isTrue();
        assertThat(checkOutResponse.data()).isNotNull();

        Order checkOutOrder = orderRepository.findWithUserAndOrderItemsAndMenuAndRestaurantById(cart.getId()).orElseThrow();
        assertThat(checkOutOrder.getStatus()).isEqualTo(Status.CREATED);
        assertThat(checkOutOrder.getOrderNumber()).isNotBlank();
        assertThat(checkOutOrder.getCheckedOutAt()).isNotNull();
        assertThat(checkOutOrder.getDeliveryAddressText()).isNotBlank();

        assertThat(checkOutOrder.getDistanceKm()).isNotNull();
        assertThat(checkOutOrder.getDeliveryFee()).isNotNull();
        assertThat(checkOutOrder.getDeliveryFee()).isGreaterThan(BigDecimal.ZERO);

        assertThat(checkOutOrder.getTotalPrice()).isGreaterThanOrEqualTo(checkOutOrder.getDeliveryFee());

        BigDecimal recomputedFinal = pricingStrategyFactory.getStrategy(customerLevelEvaluator.evaluate(checkOutOrder.getUser().getTotalScore()))
                .calculateFinalPrice(checkOutOrder);

        assertThat(checkOutOrder.getTotalPrice().subtract(checkOutOrder.getDeliveryFee()))
                .isEqualByComparingTo(recomputedFinal);

    }

    @Test
    void update_order_status_to_paid_successfully() throws Exception{

        UserResponseDto user = createUser();
        OrderRequestDto orderRequestDto = generateOrderItem(user);

        CartRequestDto cartRequestDto = new CartRequestDto(user.id(),
                10L,
                orderRequestDto.restaurantId(),
                orderRequestDto.orderItemList());

        String idempotencyKey = UUID.randomUUID().toString();
        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/orders")
                                .header("Authorization",TEST_JWT_TOKEN)
                                .header("Idempotency-Key", idempotencyKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartRequestDto))

                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderResponseDto> cartResponse =
                objectMapper.readValue(
                        content,
                        new TypeReference<>() {}
                );
        assertThat(cartResponse.success()).isTrue();
        assertThat(cartResponse.data()).isNotNull();

        Long cartId = cartResponse.data().orderId();
        Order cart = orderRepository.findWithUserAndOrderItemsAndMenuAndRestaurantById(cartId).orElseThrow();

        cart.setStatus(Status.CREATED);
        Order order = orderRepository.saveAndFlush(cart);
        assertThat(order.getStatus()).isEqualTo(Status.CREATED);

        Order persistedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(persistedOrder.getStatus()).isEqualTo(Status.CREATED);

        String orderContent = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/orders/update/admin/" + order.getId())
                                .header("Authorization",TEST_JWT_ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderStatusResponseDto> orderResponse =
                objectMapper.readValue(
                        orderContent,
                        new TypeReference<>() {}
                );
        assertThat(orderResponse.success()).isTrue();
        assertThat(orderResponse.data()).isNotNull();

        Order foundOrder = orderRepository.findById(orderResponse.data().orderId()).orElseThrow();
        assertThat(orderResponse.data().orderId()).isEqualTo(order.getId());
        assertThat(foundOrder.getStatus()).isEqualTo(Status.PAID);
        assertThat(orderResponse.data().orderStatus()).isEqualTo(Status.PAID);
        assertThat(foundOrder.getUpdatedAt()).isAfterOrEqualTo(order.getUpdatedAt());
    }

    @Test
    void cancel_order_with_initial_status_paid_successfully() throws Exception{

        UserResponseDto user = createUser();
        OrderRequestDto orderRequestDto = generateOrderItem(user);

        CartRequestDto cartRequestDto = new CartRequestDto(user.id(),
                10L,
                orderRequestDto.restaurantId(),
                orderRequestDto.orderItemList());

        String idempotencyKey = UUID.randomUUID().toString();
        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/orders")
                                .header("Authorization",TEST_JWT_TOKEN)
                                .header("Idempotency-Key", idempotencyKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartRequestDto))

                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderResponseDto> cartResponse =
                objectMapper.readValue(
                        content,
                        new TypeReference<>() {}
                );
        assertThat(cartResponse.success()).isTrue();
        assertThat(cartResponse.data()).isNotNull();

        Long cartId = cartResponse.data().orderId();
        Order cart = orderRepository.findWithUserAndOrderItemsAndMenuAndRestaurantById(cartId).orElseThrow();

        cart.setStatus(Status.CREATED);
        Order order = orderRepository.saveAndFlush(cart);
        assertThat(order.getStatus()).isEqualTo(Status.CREATED);

        Order persistedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(persistedOrder.getStatus()).isEqualTo(Status.CREATED);

        String orderContent = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/orders/update/admin/" + order.getId())
                                .header("Authorization",TEST_JWT_ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderStatusResponseDto> orderResponse =
                objectMapper.readValue(
                        orderContent,
                        new TypeReference<>() {}
                );
        assertThat(orderResponse.success()).isTrue();
        assertThat(orderResponse.data()).isNotNull();

        Order foundOrder = orderRepository.findById(orderResponse.data().orderId()).orElseThrow();
        assertThat(orderResponse.data().orderId()).isEqualTo(order.getId());
        assertThat(foundOrder.getStatus()).isEqualTo(Status.PAID);
        assertThat(orderResponse.data().orderStatus()).isEqualTo(Status.PAID);
        assertThat(foundOrder.getUpdatedAt()).isAfterOrEqualTo(order.getUpdatedAt());

        String orderContentCancel = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/orders/cancel/admin/" + foundOrder.getId())
                                .header("Authorization",TEST_JWT_ADMIN_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<OrderStatusResponseDto> orderContentUpdatedToCancelResponse =
                objectMapper.readValue(
                        orderContentCancel,
                        new TypeReference<>() {}
                );
        assertThat(orderContentUpdatedToCancelResponse.success()).isTrue();
        assertThat(orderContentUpdatedToCancelResponse.data()).isNotNull();

        Order foundCancelOrder = orderRepository.findById(orderResponse.data().orderId()).orElseThrow();
        assertThat(orderContentUpdatedToCancelResponse.data().orderId()).isEqualTo(order.getId());
        assertThat(foundCancelOrder.getStatus()).isEqualTo(Status.CANCELLED);
        assertThat(orderContentUpdatedToCancelResponse.data().orderStatus()).isEqualTo(Status.CANCELLED);
        assertThat(foundCancelOrder.getUpdatedAt()).isAfterOrEqualTo(foundOrder.getUpdatedAt());


    }




}
