package com.crms39.crms39.controller;

import com.crms39.crms39.model.ClassSession;
import com.crms39.crms39.model.User;
import com.crms39.crms39.service.CRMSContext;
import com.crms39.crms39.service.InvalidScheduleException;
import com.crms39.crms39.service.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ManageRoutineController {

    @FXML
    private TableView<ClassSession> routineTable;

    @FXML
    private TableColumn<ClassSession, String> dayColumn;

    @FXML
    private TableColumn<ClassSession, String> startTimeColumn;

    @FXML
    private TableColumn<ClassSession, String> endTimeColumn;

    @FXML
    private TableColumn<ClassSession, String> subjectColumn;

    @FXML
    private TableColumn<ClassSession, String> teacherColumn;

    @FXML
    private TableColumn<ClassSession, String> roomColumn;

    @FXML
    private TextField dayField;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextField teacherField;

    @FXML
    private TextField roomField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        dayColumn.setCellValueFactory(
                new PropertyValueFactory<>("day")
        );
        startTimeColumn.setCellValueFactory(
                new PropertyValueFactory<>("startTime")
        );
        endTimeColumn.setCellValueFactory(
                new PropertyValueFactory<>("endTime")
        );
        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>("subject")
        );
        teacherColumn.setCellValueFactory(
                new PropertyValueFactory<>("teacher")
        );
        roomColumn.setCellValueFactory(
                new PropertyValueFactory<>("room")
        );

        refreshRoutine();
    }

    @FXML
    private void handleAddSession() {
        if (hasBlankField()) {
            showStatus("Complete every session field.", false);
            return;
        }

        Optional<User> result = UserSession.getCurrentUser();

        if (result.isEmpty()) {
            showStatus("No user is currently logged in.", false);
            return;
        }

        ClassSession session = new ClassSession(
                dayField.getText().trim(),
                startTimeField.getText().trim(),
                endTimeField.getText().trim(),
                subjectField.getText().trim(),
                teacherField.getText().trim(),
                roomField.getText().trim()
        );

        try {
            CRMSContext.getRoutineManager()
                    .addSession(result.get(), session);

            clearFields();
            refreshRoutine();

            showStatus("Class session added successfully.", true);

        } catch (InvalidScheduleException exception) {
            showStatus(exception.getMessage(), false);
        }
    }

    @FXML
    private void handleEditSelected() {
        ClassSession selectedSession = getSelectedSession();

        if (selectedSession == null) {
            return;
        }

        dayField.setText(selectedSession.getDay());
        startTimeField.setText(selectedSession.getStartTime());
        endTimeField.setText(selectedSession.getEndTime());
        subjectField.setText(selectedSession.getSubject());
        teacherField.setText(selectedSession.getTeacher());
        roomField.setText(selectedSession.getRoom());

        showStatus(
                "Edit the values, then click Update Selected.",
                true
        );
    }

    @FXML
    private void handleUpdateSession() {
        ClassSession selectedSession = getSelectedSession();

        if (selectedSession == null || hasBlankField()) {
            if (selectedSession != null) {
                showStatus("Complete every session field.", false);
            }
            return;
        }

        Optional<User> result = UserSession.getCurrentUser();

        if (result.isEmpty()) {
            showStatus("No user is currently logged in.", false);
            return;
        }

        try {
            CRMSContext.getRoutineManager().updateSession(
                    result.get(),
                    selectedSession,
                    dayField.getText().trim(),
                    startTimeField.getText().trim(),
                    endTimeField.getText().trim(),
                    subjectField.getText().trim(),
                    teacherField.getText().trim(),
                    roomField.getText().trim()
            );

            clearFields();
            refreshRoutine();

            showStatus("Class session updated successfully.", true);

        } catch (InvalidScheduleException exception) {
            showStatus(exception.getMessage(), false);
        }
    }

    @FXML
    private void handleDeleteSelected() {
        ClassSession selectedSession = getSelectedSession();

        if (selectedSession == null) {
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Class Session");
        confirmation.setHeaderText("Delete the selected session?");
        confirmation.setContentText(
                selectedSession.getDay()
                        + " | " + selectedSession.getStartTime()
                        + " | " + selectedSession.getSubject()
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        Optional<User> userResult = UserSession.getCurrentUser();

        if (userResult.isEmpty()) {
            showStatus("No user is currently logged in.", false);
            return;
        }

        try {
            CRMSContext.getRoutineManager()
                    .deleteSession(userResult.get(), selectedSession);

            clearFields();
            refreshRoutine();

            showStatus("Class session deleted successfully.", true);

        } catch (InvalidScheduleException exception) {
            showStatus(exception.getMessage(), false);
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ManageRoutineController.class.getResource(
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

    private ClassSession getSelectedSession() {
        ClassSession selectedSession = routineTable
                .getSelectionModel()
                .getSelectedItem();

        if (selectedSession == null) {
            showStatus(
                    "Select a class session from the table first.",
                    false
            );
        }

        return selectedSession;
    }

    private void refreshRoutine() {
        ObservableList<ClassSession> sessions =
                FXCollections.observableArrayList(
                        CRMSContext.getRoutineManager()
                                .getSchedule()
                                .getSessions()
                );

        routineTable.setItems(sessions);
    }

    private boolean hasBlankField() {
        return dayField.getText().isBlank()
                || startTimeField.getText().isBlank()
                || endTimeField.getText().isBlank()
                || subjectField.getText().isBlank()
                || teacherField.getText().isBlank()
                || roomField.getText().isBlank();
    }

    private void clearFields() {
        dayField.clear();
        startTimeField.clear();
        endTimeField.clear();
        subjectField.clear();
        teacherField.clear();
        roomField.clear();
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);

        if (success) {
            statusLabel.setStyle("-fx-text-fill: green;");
            return;
        }

        statusLabel.setStyle("-fx-text-fill: red;");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Class Session");
        alert.setHeaderText("The class session was not saved.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("CRMS Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);

        alert.showAndWait();
    }
}