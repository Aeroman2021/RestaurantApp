package com.restaurant.app.demo.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restaurant.app.demo.controller.ApiResponse;
import com.restaurant.app.demo.model.dto.cart.CartRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.dto.user.UserResponseDto;
import com.restaurant.app.demo.model.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class OrderControllerTest extends BaseIntegrationTest {


    @Test
    void upsertCart_successfully() throws Exception{
        UserResponseDto user = createUser();
        List<OrderItemRequestDto> orderItemRequestDtoList = generateOrderItem(user).orderItemList();
        CartRequestDto cartRequestDto = new CartRequestDto(user.id(),10L,orderItemRequestDtoList);

        String content = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/orders")
                        .header("Authorization",TEST_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequestDto))

        ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponse<Order> response =
                objectMapper.readValue(
                        content,
                        new TypeReference<>() {}
                );
        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNotNull();
    }



    @Test
    void checkout_order_successfully() throws Exception {

        long orderId = 5L;
        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/api/v1/orders/checkOut/" + orderId)
                                .header("Authorization",TEST_JWT_TOKEN)
                                .header("Idempotency-Key", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
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

}
