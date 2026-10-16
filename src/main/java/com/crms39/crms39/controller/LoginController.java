package com.crms39.crms39.controller;

import com.crms39.crms39.model.User;
import com.crms39.crms39.service.AuthenticationService;
import com.crms39.crms39.service.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private final AuthenticationService authenticationService =
            new AuthenticationService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showMessage("Enter both username and password.", false);
            return;
        }

        Optional<User> result =
                authenticationService.authenticate(username, password);

        if (result.isPresent()) {
            User user = result.get();

            UserSession.start(user);
            openDashboard();
        } else {
            showMessage("Invalid username or password.", false);
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource(
                            "/com/crms39/crms39/dashboard-view.fxml"
                    )
            );

            Scene scene = new Scene(loader.load(), 600, 500);

            Stage stage = (Stage) usernameField
                    .getScene()
                    .getWindow();

            stage.setTitle("CRMS Dashboard");
            stage.setScene(scene);
        } catch (IOException exception) {
            showMessage(
                    "Could not open the dashboard screen.",
                    false
            );
        }
    }

    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
}