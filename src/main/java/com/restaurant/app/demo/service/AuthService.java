package com.restaurant.app.demo.service;


import com.restaurant.app.demo.model.dto.AuthResponse;
import com.restaurant.app.demo.model.dto.LoginRequest;
import com.restaurant.app.demo.model.dto.RegisterRequest;
import com.restaurant.app.demo.model.dto.UserResponseDto;
import com.restaurant.app.demo.model.entity.Role;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.repository.RoleRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.security.CustomUserDetailService;
import com.restaurant.app.demo.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private  final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager manager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.manager = manager;
        this.jwtService = jwtService;
    }

    public UserResponseDto register(RegisterRequest registerRequest){
        Role roleUser = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found."));
        User user = new User();
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setUserName(registerRequest.userName());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        Optional.ofNullable(registerRequest.email()).ifPresent(user::setEmail);
        Optional.ofNullable(registerRequest.phone()).ifPresent(user::setPhone);
        user.setRoles(Set.of(roleUser));
        user.setActive(true);
        User result = userRepository.save(user);
        return new UserResponseDto(result.getId(), result.getFirstName(), result.getLastName(), result.getUserName());
    }

    public AuthResponse login(LoginRequest loginRequest){
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.userName(),
                        loginRequest.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
    }
}
