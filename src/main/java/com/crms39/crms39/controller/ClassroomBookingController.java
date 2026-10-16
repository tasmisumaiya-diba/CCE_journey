package com.crms39.crms39.controller;

import com.crms39.crms39.model.Classroom;
import com.crms39.crms39.model.RoomBooking;
import com.crms39.crms39.model.User;
import com.crms39.crms39.service.CRMSContext;
import com.crms39.crms39.service.ClassroomUnavailableException;
import com.crms39.crms39.service.UserSession;
import javafx.scene.control.ButtonType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Optional;

public class ClassroomBookingController {

    @FXML
    private TableView<RoomBooking> bookingTable;

    @FXML
    private TableColumn<RoomBooking, String> roomColumn;

    @FXML
    private TableColumn<RoomBooking, String> dayColumn;

    @FXML
    private TableColumn<RoomBooking, String> startTimeColumn;

    @FXML
    private TableColumn<RoomBooking, String> endTimeColumn;

    @FXML
    private TableColumn<RoomBooking, String> purposeColumn;

    @FXML
    private TableColumn<RoomBooking, String> bookedByColumn;

    @FXML
    private TextField roomField;

    @FXML
    private TextField dayField;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField purposeField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        roomColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getClassroom()
                                .getRoomCode()
                )
        );

        dayColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDay()
                )
        );

        startTimeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getStartTime()
                                .toString()
                )
        );

        endTimeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getEndTime()
                                .toString()
                )
        );

        purposeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPurpose()
                )
        );

        bookedByColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getBookedBy()
                )
        );

        refreshBookings();
    }

    @FXML
    private void handleBookClassroom() {
        if (hasBlankField()) {
            showStatus("Complete every booking field.", false);
            return;
        }

        Optional<User> result = UserSession.getCurrentUser();

        if (result.isEmpty()) {
            showStatus("No user is currently logged in.", false);
            return;
        }

        try {
            LocalTime startTime = LocalTime.parse(
                    startTimeField.getText().trim()
            );

            LocalTime endTime = LocalTime.parse(
                    endTimeField.getText().trim()
            );

            Classroom classroom = new Classroom(
                    roomField.getText().trim(),
                    "Campus"
            );

            RoomBooking booking = new RoomBooking(
                    classroom,
                    dayField.getText().trim(),
                    startTime,
                    endTime,
                    purposeField.getText().trim(),
                    result.get().getDisplayName()
            );

            CRMSContext.getRoutineManager()
                    .addRoomBooking(result.get(), booking);

            clearFields();
            refreshBookings();

            showStatus("Classroom booked successfully.", true);

        } catch (ClassroomUnavailableException exception) {
            showStatus(exception.getMessage(), false);

        } catch (Exception exception) {
            showStatus(
                    "Use time format HH:mm, for example 14:15.",
                    false
            );
        }
    }
    @FXML
    private void handleCancelBooking() {
        RoomBooking selectedBooking = bookingTable
                .getSelectionModel()
                .getSelectedItem();

        if (selectedBooking == null) {
            showStatus(
                    "Select a classroom booking before cancelling it.",
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
        confirmation.setTitle("Cancel Classroom Booking");
        confirmation.setHeaderText("This action cannot be undone.");
        confirmation.setContentText(
                "Cancel the booking for "
                        + selectedBooking.getClassroom().getRoomCode()
                        + " on " + selectedBooking.getDay()
                        + "?"
        );

        Optional<ButtonType> choice = confirmation.showAndWait();

        if (choice.isEmpty() || choice.get() != ButtonType.OK) {
            return;
        }

        try {
            CRMSContext.getRoutineManager().cancelRoomBooking(
                    result.get(),
                    selectedBooking
            );

            refreshBookings();
            bookingTable.getSelectionModel().clearSelection();

            showStatus(
                    "Classroom booking cancelled successfully.",
                    true
            );

        } catch (ClassroomUnavailableException exception) {
            showStatus(exception.getMessage(), false);
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ClassroomBookingController.class.getResource(
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

    private void refreshBookings() {
        ObservableList<RoomBooking> bookings =
                FXCollections.observableArrayList(
                        CRMSContext.getRoutineManager()
                                .getRoomBookings()
                );

        bookingTable.setItems(bookings);
    }

    private boolean hasBlankField() {
        return roomField.getText().isBlank()
                || dayField.getText().isBlank()
                || startTimeField.getText().isBlank()
                || endTimeField.getText().isBlank()
                || purposeField.getText().isBlank();
    }

    private void clearFields() {
        roomField.clear();
        dayField.clear();
        startTimeField.clear();
        endTimeField.clear();
        purposeField.clear();
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