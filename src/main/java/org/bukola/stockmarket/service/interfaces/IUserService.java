package org.bukola.stockmarket.service.interfaces;

import org.bukola.stockmarket.dto.LoginRequest;
import org.bukola.stockmarket.dto.RegisterRequest;
import org.bukola.stockmarket.model.User;

public interface IUserService {
    User registerUser (RegisterRequest registerRequest);
    User authenticateUser(LoginRequest loginRequest);
}
