package com.crms39.crms39.controller;

import com.crms39.crms39.model.Announcement;
import com.crms39.crms39.model.User;
import com.crms39.crms39.service.CRMSContext;
import com.crms39.crms39.service.UserSession;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AnnouncementController {

    @FXML
    private ListView<Announcement> announcementListView;

    @FXML
    private VBox postAnnouncementBox;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea messageArea;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        refreshAnnouncements();
        configurePostPermission();
    }

    private void configurePostPermission() {
        Optional<User> result = UserSession.getCurrentUser();

        boolean canPost = result.isPresent()
                && result.get().canPostAnnouncements();

        postAnnouncementBox.setVisible(canPost);
        postAnnouncementBox.setManaged(canPost);

        if (!canPost) {
            statusLabel.setText(
                    "Only the Class Representative can post announcements."
            );
        }
    }

    @FXML
    private void handlePostAnnouncement() {
        String title = titleField.getText();
        String message = messageArea.getText();

        if (title.isBlank() || message.isBlank()) {
            showStatus(
                    "Enter both an announcement title and message.",
                    false
            );
            return;
        }

        Optional<User> result = UserSession.getCurrentUser();

        if (result.isEmpty()) {
            showStatus("No user is currently logged in.", false);
            return;
        }

        User user = result.get();

        Announcement announcement = new Announcement(
                title,
                message,
                user.getDisplayName(),
                LocalDateTime.now()
        );

        try {
            CRMSContext.getRoutineManager()
                    .postAnnouncement(user, announcement);

            titleField.clear();
            messageArea.clear();

            refreshAnnouncements();

            showStatus("Announcement posted successfully.", true);

        } catch (SecurityException exception) {
            showStatus(exception.getMessage(), false);
        }
    }
    @FXML
    private void handleDeleteAnnouncement() {
        Announcement selectedAnnouncement = announcementListView
                .getSelectionModel()
                .getSelectedItem();

        if (selectedAnnouncement == null) {
            showStatus(
                    "Select an announcement before deleting it.",
                    false
            );
            return;
        }

        Optional<User> result = UserSession.getCurrentUser();

        if (result.isEmpty()) {
            showStatus("No user is currently logged in.", false);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Announcement");
        confirmation.setHeaderText("This action cannot be undone.");
        confirmation.setContentText(
                "Delete \"" + selectedAnnouncement.getTitle() + "\"?"
        );

        Optional<ButtonType> choice = confirmation.showAndWait();

        if (choice.isEmpty() || choice.get() != ButtonType.OK) {
            return;
        }

        try {
            CRMSContext.getRoutineManager().deleteAnnouncement(
                    result.get(),
                    selectedAnnouncement
            );

            refreshAnnouncements();
            announcementListView.getSelectionModel().clearSelection();

            showStatus("Announcement deleted successfully.", true);

        } catch (IllegalArgumentException | IllegalStateException exception) {
            showStatus(exception.getMessage(), false);
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AnnouncementController.class.getResource(
                            "/com/crms39/crms39/dashboard-view.fxml"
                    )
            );

            Scene scene = new Scene(loader.load(), 600, 450);

            Stage stage = (Stage) backButton
                    .getScene()
                    .getWindow();

            stage.setTitle("CRMS Dashboard");
            stage.setScene(scene);

        } catch (IOException exception) {
            showError("Could not return to the dashboard.");
        }
    }

    private void refreshAnnouncements() {
        ObservableList<Announcement> announcements =
                FXCollections.observableArrayList(
                        CRMSContext.getRoutineManager()
                                .getAnnouncements()
                );

        announcementListView.setItems(announcements);
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);

        if (success) {
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
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