package com.crms39.crms39.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConnectionTest {

    @Test
    void connectsToCrms39Database() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection);
            assertNotNull(connection.getMetaData().getDatabaseProductName());
        }
    }
}