package org.bukola.stockmarket.controller;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.AuthResponse;
import org.bukola.stockmarket.dto.LoginRequest;
import org.bukola.stockmarket.dto.RegisterRequest;
import org.bukola.stockmarket.enums.Role;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.repository.UserRepository;
import org.bukola.stockmarket.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                Role.USER
        );
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", Role.USER);
        String token = JwtUtil.generateToken(user.getUsername(), claims);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User existingUser = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", Role.USER); // Add custom claims if needed
        String token = JwtUtil.generateToken(user.getUsername(), claims);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}