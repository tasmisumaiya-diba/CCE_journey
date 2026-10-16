package com.crms39.crms39.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String CONFIG_FILE =
            "/com/crms39/crms39/database.properties";

    private static final Properties DATABASE_PROPERTIES =
            loadProperties();

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                getRequiredProperty("db.url"),
                getRequiredProperty("db.user"),
                getRequiredProperty("db.password")
        );
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream =
                     DatabaseConnection.class.getResourceAsStream(
                             CONFIG_FILE
                     )) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Database settings file was not found."
                );
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read database settings.",
                    exception
            );
        }
    }

    private static String getRequiredProperty(String propertyName) {
        String value = DATABASE_PROPERTIES.getProperty(propertyName);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing database setting: " + propertyName
            );
        }

        return value;
    }
}