package com.restaurant.app.demo.order;

import com.restaurant.app.demo.controller.ApiResponse;
import com.restaurant.app.demo.model.dto.AuthResponse;
import com.restaurant.app.demo.model.dto.LoginRequest;
import com.restaurant.app.demo.model.dto.RegisterRequest;
import com.restaurant.app.demo.model.dto.UserResponseDto;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.entity.Category;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.repository.OrderRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.security.CustomUserDetailService;
import com.restaurant.app.demo.security.JwtService;
import com.restaurant.app.demo.service.AuthService;
import com.restaurant.app.demo.service.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MenuItemRepository menuItemRepository;

    @Autowired
    AuthService authService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void create_order_successfull() {

        // REGISTER
        RegisterRequest registerRequest =
                new RegisterRequest(
                        "Aeroman2222",
                        "jdkncjdsnc",
                        "Sahand",
                        "Borazjani",
                        "09371893687",
                        "malakoutiMohsen.aero@gmail.com"
                );

        UserResponseDto result = authService.register(registerRequest);

// LOGIN (HTTP واقعی)
        LoginRequest login =
                new LoginRequest("Aeroman2222", "jdkncjdsnc");

        ResponseEntity<AuthResponse> loginResponse =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/api/auth/login",
                        login,
                        AuthResponse.class
                );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String token = Objects.requireNonNull(loginResponse.getBody()).token();
        System.out.println("JWT = " + token);

// DATA SETUP
        Category fastFood = new Category();
        fastFood.setName("fastFood");
        categoryService.create(fastFood);

        MenuItem pizza = new MenuItem();
        pizza.setName("Pizza");
        pizza.setPrice(new BigDecimal("100"));
        pizza.setCategory(fastFood);

        MenuItem burger = new MenuItem();
        burger.setName("Burger");
        burger.setPrice(new BigDecimal("200"));
        burger.setCategory(fastFood);

        menuItemRepository.saveAll(List.of(pizza, burger));

// ORDER REQUEST
        OrderRequestDto request =
                new OrderRequestDto(
                        result.id(),
                        Status.CREATED,
                        List.of(
                                new OrderItemRequestDto(pizza.getId(), 2),
                                new OrderItemRequestDto(burger.getId(), 1)
                        )
                );

// AUTH HEADER (درست)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // ⬅ فقط JWT، نه "Bearer ..."

        HttpEntity<OrderRequestDto> entity =
                new HttpEntity<>(request, headers);

// CALL
        ResponseEntity<ApiResponse<OrderResponseDto>> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/api/orders",
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

// ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

}

}
