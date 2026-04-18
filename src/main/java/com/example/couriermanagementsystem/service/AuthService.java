package com.example.couriermanagementsystem.service;

import com.example.couriermanagementsystem.constants.ApiMessages;
import com.example.couriermanagementsystem.config.JwtUtil;
import com.example.couriermanagementsystem.dto.ApiResponse;
import com.example.couriermanagementsystem.dto.AuthResponse;
import com.example.couriermanagementsystem.dto.LoginRequest;
import com.example.couriermanagementsystem.dto.RegisterRequest;
import com.example.couriermanagementsystem.entity.User;
import com.example.couriermanagementsystem.enums.UserRole;
import com.example.couriermanagementsystem.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public ApiResponse<Void> registerCustomer(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException(ApiMessages.EMAIL_EXISTS);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);

        userRepository.save(user);
        return new ApiResponse<>(true, ApiMessages.CUSTOMER_REGISTERED, null);
    }

    public ApiResponse<Void> createUserByAdmin(RegisterRequest request, UserRole role) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException(ApiMessages.EMAIL_EXISTS);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);
        String message = role == UserRole.MANAGER
                ? ApiMessages.MANAGER_CREATED
                : ApiMessages.AGENT_CREATED;
        return new ApiResponse<>(true, message, null);
    }

    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String role = authentication.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            String plainRole = role.replace("ROLE_", "");
            String token = jwtUtil.generateToken(request.getEmail(), plainRole);
            AuthResponse authResponse = new AuthResponse(token, plainRole);

            return new ApiResponse<>(true, ApiMessages.LOGIN_SUCCESS, authResponse);
        } catch (AuthenticationException ex) {
            return new ApiResponse<>(false, ApiMessages.INVALID_CREDENTIALS, null);
        }
    }
}
