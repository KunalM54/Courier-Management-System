package com.example.CourierManagement.controller;

import com.example.CourierManagement.dto.LoginRequest;
import com.example.CourierManagement.dto.RegisterRequest;
import com.example.CourierManagement.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // PUBLIC REGISTER (CUSTOMER ONLY)
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.registerCustomer(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
