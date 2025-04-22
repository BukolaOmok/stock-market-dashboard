package org.bukola.stockmarket.service;

import lombok.RequiredArgsConstructor;
import org.bukola.stockmarket.dto.LoginRequest;
import org.bukola.stockmarket.dto.RegisterRequest;
import org.bukola.stockmarket.enums.Role;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.repository.UserRepository;
import org.bukola.stockmarket.service.interfaces.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username exists");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        return userRepository.save(user);
    }

    public User authenticateUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
