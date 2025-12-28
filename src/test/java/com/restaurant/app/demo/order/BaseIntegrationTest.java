package com.restaurant.app.demo.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.dto.user.RegisterRequest;
import com.restaurant.app.demo.model.dto.user.UserResponseDto;
import com.restaurant.app.demo.model.entity.Category;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.repository.RoleRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.security.CustomUserDetailService;
import com.restaurant.app.demo.security.JwtService;
import com.restaurant.app.demo.service.CategoryService;
import com.restaurant.app.demo.service.impl.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    MenuItemRepository menuItemRepository;


    @Autowired
    CategoryService categoryService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected AuthService authService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    protected static final String TEST_JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBZXJvbWFuMjAyNSIsInJvbGVzIjpbIlJPTEVfQ1VTVE9NRVIiXSwiaWF0IjoxNzY2OTA5NDc0LCJleHAiOjE3NjY5MTMwNzR9.hvnQexWVYWZVqBHXNbdQ1x6r8XOGTV_hwzxpLbFWipM";

    protected UserResponseDto createUser() {
        RegisterRequest registerRequest =
                new RegisterRequest(
                        "Ronin2025",
                        "jdkncjdsnc",
                        "Sahand",
                        "Borazjani",
                        "09371893687",
                        "malakoutiMohsen.aero@gmail.com",
                        CustomerLevel.REGULAR);
        return authService.register(registerRequest);
    }

    protected String generateToken(UserResponseDto UserResponseDto) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(UserResponseDto.username());
        return jwtService.generateToken(userDetails);
    }

    protected OrderRequestDto generateOrderItem(UserResponseDto userResponseDto){
        Category fastFood = new Category();
        fastFood.setName("fastFood");
        categoryService.create(fastFood);

        MenuItem pizza = new MenuItem();
        pizza.setName("Pizza");
        pizza.setPrice(new BigDecimal("100"));
        pizza.setCategory(fastFood);
        MenuItem savedPizza = menuItemRepository.save(pizza);
        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto(savedPizza.getId(),2);

        MenuItem burger = new MenuItem();
        burger.setName("Burger");
        burger.setPrice(new BigDecimal("200"));
        burger.setCategory(fastFood);
        MenuItem savedBurger = menuItemRepository.save(burger);
        OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto(savedBurger.getId(),3);

        return new OrderRequestDto(userResponseDto.id(), Status.CREATED,List.of(orderItemRequestDto, orderItemRequestDto2));

    }

}
