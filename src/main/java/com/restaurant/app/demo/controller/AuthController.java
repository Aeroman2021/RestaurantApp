package com.restaurant.app.demo.controller;



import com.restaurant.app.demo.model.dto.AuthResponse;
import com.restaurant.app.demo.model.dto.LoginRequest;
import com.restaurant.app.demo.model.dto.RegisterRequest;
import com.restaurant.app.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid  @RequestBody RegisterRequest request){
        authService.register(request);
        return ApiResponse.ok(null,"user registered successfully");
    }

    @PostMapping("/login")
    public ApiResponse<?> register(@Valid  @RequestBody LoginRequest request){
        AuthResponse TOKEN = authService.login(request);
        return ApiResponse.ok(TOKEN,"login successful");
    }
}
