package com.restaurant.app.demo.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.restaurant.app.demo.controller.ApiResponse;
import com.restaurant.app.demo.model.dto.UserResponseDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class OrderControllerTest extends BaseIntegrationTest {


    @Test
    void create_order_successfully() throws Exception {

        UserResponseDto user = createUser();
        String token = generateToken(user);

    // ORDER REQUEST
        OrderRequestDto orderRequestDto = generateOrderItem(user);

        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/orders")
                                .header("Authorization",token)
                                .header("Idempotency-Key", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequestDto))
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
