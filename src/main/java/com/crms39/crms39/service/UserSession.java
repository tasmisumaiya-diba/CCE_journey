package com.crms39.crms39.service;

import com.crms39.crms39.model.User;

import java.util.Optional;

public final class UserSession {

    private static User currentUser;

    private UserSession() {
    }

    public static void start(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "A valid user is required to start a session."
            );
        }

        currentUser = user;
    }

    public static Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public static void end() {
        currentUser = null;
    }
}