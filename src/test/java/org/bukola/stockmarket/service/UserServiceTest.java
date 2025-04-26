package org.bukola.stockmarket.service;

import org.bukola.stockmarket.dto.LoginRequest;
import org.bukola.stockmarket.dto.RegisterRequest;
import org.bukola.stockmarket.enums.Role;
import org.bukola.stockmarket.model.User;
import org.bukola.stockmarket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Given there is a user")
@Nested
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserService userService;

    @Nested
    @DisplayName("When user tries to register")
    class RegisterUserTest {
        @Test
        @DisplayName("Then user cannot use an existing username")
        void registerUser_throwsException_WhenUsernameExists() {
            RegisterRequest request = new RegisterRequest();

            when(userRepository.existsByUsername("Bukola")).thenReturn(true);
            assertThrows(RuntimeException.class, () -> userService.registerUser(request) );
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Then user should be registered with encoded password")
        void registerUser_saveUser_whenUserNameIsUnique () {
            RegisterRequest request = new RegisterRequest("Bukola", "password123");
            String encodedPassword = "$2a$10$encodedPassword";

            when(userRepository.existsByUsername("Bukola")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.registerUser(request);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertEquals("Bukola", savedUser.getUsername());
            assertNotNull(encodedPassword, savedUser.getPassword());
            assertEquals(Role.USER, savedUser.getRole());
        }
    }

    @Nested
    @DisplayName("When user tries to log in")
    class AuthenticateUserTest {

        @DisplayName("Then reject invalid password")
        @Test
        void authenticateUser_ThrowsException_WhenPasswordIsWrong() {
            LoginRequest loginRequest = new LoginRequest();
            User existingUser = new User("Bukola", "$2a$10$encodedPassword", Role.USER);

            when(userRepository.findByUsername("Bukola")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", existingUser.getPassword())).thenReturn(false);

            assertThrows(RuntimeException.class, () -> userService.authenticateUser(loginRequest));
            verify(userRepository, never()).save(any());
        }
    }

}