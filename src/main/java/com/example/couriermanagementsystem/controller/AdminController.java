package com.example.couriermanagementsystem.controller;

import com.example.couriermanagementsystem.dto.ApiResponse;
import com.example.couriermanagementsystem.dto.RegisterRequest;
import com.example.couriermanagementsystem.enums.UserRole;
import com.example.couriermanagementsystem.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-manager")
    public ApiResponse<Void> createManager(@RequestBody RegisterRequest request) {
        return authService.createUserByAdmin(request, UserRole.MANAGER);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-agent")
    public ApiResponse<Void> createAgent(@RequestBody RegisterRequest request) {
        return authService.createUserByAdmin(request, UserRole.DELIVERY_AGENT);
    }
}
