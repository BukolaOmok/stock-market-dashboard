package org.bukola.stockmarket.controller;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.AuthResponse;
import org.bukola.stockmarket.dto.LoginRequest;
import org.bukola.stockmarket.dto.RegisterRequest;
import org.bukola.stockmarket.enums.Role;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.security.JwtUtil;
import org.bukola.stockmarket.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private IUserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", Role.USER);
        String token = JwtUtil.generateToken(user.getUsername(), claims);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.authenticateUser(loginRequest);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name()); // Add the role to the claims
        String token = JwtUtil.generateToken(user.getUsername(), claims); // Use instance method

        return ResponseEntity.ok(new AuthResponse(token));
    }
}