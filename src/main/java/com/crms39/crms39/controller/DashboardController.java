package com.crms39.crms39.controller;

import com.crms39.crms39.model.User;
import com.crms39.crms39.service.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label permissionLabel;

    @FXML
    private Button viewRoutineButton;

    @FXML
    private Button viewAnnouncementsButton;

    @FXML
    private Button manageRoutineButton;

    @FXML
    private Button bookClassroomButton;

    @FXML
    private Button postAnnouncementButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        Optional<User> result = UserSession.getCurrentUser();

        if (result.isEmpty()) {
            welcomeLabel.setText("No user is currently logged in.");
            return;
        }

        User user = result.get();

        welcomeLabel.setText("Welcome, " + user.getDisplayName());
        roleLabel.setText("Role: " + user.getRole());

        if (user.canEditSchedule()) {
            permissionLabel.setText(
                    "You can view and manage the class routine."
            );
        } else {
            permissionLabel.setText(
                    "You have read-only access to the class routine."
            );
        }

        manageRoutineButton.setVisible(user.canEditSchedule());
        manageRoutineButton.setManaged(user.canEditSchedule());

        bookClassroomButton.setVisible(user.canEditSchedule());
        bookClassroomButton.setManaged(user.canEditSchedule());

        postAnnouncementButton.setVisible(
                user.canPostAnnouncements()
        );
        postAnnouncementButton.setManaged(
                user.canPostAnnouncements()
        );
    }

    @FXML
    private void handleViewRoutine() {
        openScreen(
                "/com/crms39/crms39/routine-view.fxml",
                "Weekly Class Routine",
                900,
                550,
                viewRoutineButton
        );
    }

    @FXML
    private void handleViewAnnouncements() {
        openScreen(
                "/com/crms39/crms39/announcement-view.fxml",
                "Class Announcements",
                900,
                550,
                viewAnnouncementsButton
        );
    }

    @FXML
    private void handleManageRoutine() {
        openScreen(
                "/com/crms39/crms39/manage-routine-view.fxml",
                "Manage Class Routine",
                1000,
                750,
                manageRoutineButton
        );
    }

    @FXML
    private void handleBookClassroom() {
        openScreen(
                "/com/crms39/crms39/classroom-booking-view.fxml",
                "Classroom Booking",
                1000,
                700,
                bookClassroomButton
        );
    }

    @FXML
    private void handleLogout() {
        UserSession.end();

        openScreen(
                "/com/crms39/crms39/login-view.fxml",
                "Class Routine Management System",
                600,
                550,
                logoutButton
        );
    }

    private void openScreen(
            String fxmlFile,
            String title,
            int width,
            int height,
            Button sourceButton
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DashboardController.class.getResource(fxmlFile)
            );

            Scene scene = new Scene(loader.load(), width, height);

            Stage stage = (Stage) sourceButton
                    .getScene()
                    .getWindow();

            stage.setTitle(title);
            stage.setScene(scene);

        } catch (IOException exception) {
            showError("Could not open the requested screen.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("CRMS Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);

        alert.showAndWait();
    }
}