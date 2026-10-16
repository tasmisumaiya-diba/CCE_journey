package com.crms39.crms39.service;

import com.crms39.crms39.model.User;
import com.crms39.crms39.repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService() {
        userRepository = new UserRepository();
    }

    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null
                || username.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        try {
            return userRepository.findByCredentials(username.trim(), password);
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not connect to the database for login.",
                    exception
            );
        }
    }
}