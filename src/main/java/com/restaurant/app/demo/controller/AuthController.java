package com.restaurant.app.demo.controller;



import com.restaurant.app.demo.model.dto.AuthResponse;
import com.restaurant.app.demo.model.dto.LoginRequest;
import com.restaurant.app.demo.model.dto.RegisterRequest;
import com.restaurant.app.demo.model.dto.UserResponseDto;
import com.restaurant.app.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid  @RequestBody RegisterRequest request){
        UserResponseDto result = authService.register(request);
        return ApiResponse.ok(result,"user registered successfully");
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid  @RequestBody LoginRequest request){
        AuthResponse TOKEN = authService.login(request);
        return ApiResponse.ok(TOKEN,"login successful");
    }
}
