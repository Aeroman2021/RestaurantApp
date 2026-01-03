package com.restaurant.app.demo.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.dto.orderItem.OrderItemRequestDto;
import com.restaurant.app.demo.model.dto.user.RegisterRequest;
import com.restaurant.app.demo.model.dto.user.UserResponseDto;
import com.restaurant.app.demo.model.entity.Category;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.Restaurant;
import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import com.restaurant.app.demo.model.entity.enums.Status;
import com.restaurant.app.demo.repository.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    protected RestaurantRepository restaurantRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private OrderRepository orderRepository;

    protected static final String TEST_JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBZXJvbWFuMjAyNSIsInJvbGVzIjpbIlJPTEVfQ1VTVE9NRVIiXSwiaWF0IjoxNzY3NDMxODkxLCJleHAiOjE3Njc0MzU0OTF9.Fs9tsRKwav_--4HNahwNdwSa0J6NdU9seekaViUdXl8";
    protected static final String TEST_JWT_ADMIN_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMYW1lcjIwMjYiLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlhdCI6MTc2NzQzMTk5MSwiZXhwIjoxNzY3NDM1NTkxfQ.1lGhfXmto_T8GKL7TpwBXxknxqf-FNpRMwtlJ_d41NY";

    protected UserResponseDto createUser() {
        RegisterRequest registerRequest =
                new RegisterRequest(
                        "Lamer2025",
                        "jdkncjdsnc",
                        "Mohsen",
                        "Malakouti",
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

        Restaurant restaurant = new Restaurant();
        restaurant.setIsActive(true);
        restaurant.setAddressText("جردن، بلوار صبا، پلاک 19");
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setLat(new BigDecimal("35.7943778"));
        restaurant.setLng(new BigDecimal("51.4236973"));
        restaurant.setName("چاپ چاپ");
        restaurant.setPhone("02175171");
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Category fastFood = new Category();
        fastFood.setName("fastFood");
        categoryService.create(fastFood);

        MenuItem pizza = new MenuItem();
        pizza.setName("Pizza");
        pizza.setPrice(new BigDecimal("100"));
        pizza.setCategory(fastFood);
        pizza.setRestaurant(savedRestaurant);
        pizza.setActive(true);
        MenuItem savedPizza = menuItemRepository.save(pizza);

        MenuItem burger = new MenuItem();
        burger.setName("Burger");
        burger.setPrice(new BigDecimal("200"));
        burger.setCategory(fastFood);
        burger.setRestaurant(savedRestaurant);
        burger.setActive(true);
        MenuItem savedBurger = menuItemRepository.save(burger);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto(savedPizza.getId(),2);
        OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto(savedBurger.getId(),3);

        Set<MenuItem> menuItems = Set.of(savedPizza, savedBurger);
        restaurant.setMenueItems(menuItems);

        return new OrderRequestDto(savedRestaurant.getId(),userResponseDto.id(), Status.CREATED,List.of(orderItemRequestDto, orderItemRequestDto2));
    }

}
