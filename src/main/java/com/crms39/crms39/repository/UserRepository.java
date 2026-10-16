package com.crms39.crms39.repository;

import com.crms39.crms39.database.DatabaseConnection;
import com.crms39.crms39.model.ClassRepresentative;
import com.crms39.crms39.model.Student;
import com.crms39.crms39.model.Teacher;
import com.crms39.crms39.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByCredentials(String username, String password)
            throws SQLException {

        String sql = "SELECT username, password, display_name, role "
                + "FROM users "
                + "WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    User user = createUser(
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("display_name"),
                            result.getString("role")
                    );

                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }

    private User createUser(String username, String password,
                            String displayName, String role)
            throws SQLException {

        return switch (role) {
            case "TEACHER" -> new Teacher(username, password, displayName);
            case "CR" -> new ClassRepresentative(username, password, displayName);
            case "STUDENT" -> new Student(username, password, displayName);
            default -> throw new SQLException("Unknown user role: " + role);
        };
    }
}