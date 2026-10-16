package com.crms39.crms39.service;

import com.crms39.crms39.model.Role;
import com.crms39.crms39.model.User;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationServiceTest {

    @Test
    void teacherCanLogInWithCorrectPassword() {
        AuthenticationService authenticationService =
                new AuthenticationService();

        Optional<User> result = authenticationService.authenticate(
                "teacher",
                "demo"
        );

        assertTrue(result.isPresent());
        assertEquals(Role.TEACHER, result.get().getRole());
    }

    @Test
    void loginFailsWithWrongPassword() {
        AuthenticationService authenticationService =
                new AuthenticationService();

        Optional<User> result = authenticationService.authenticate(
                "teacher",
                "wrong-password"
        );

        assertTrue(result.isEmpty());
    }
}