package com.crms39.crms39.controller;

import com.crms39.crms39.model.ClassSession;
import com.crms39.crms39.service.CRMSContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RoutineController {

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
    private TextField searchField;

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

        loadRoutine("");
    }

    @FXML
    private void handleSearch() {
        loadRoutine(searchField.getText());
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadRoutine("");
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    RoutineController.class.getResource(
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

    private void loadRoutine(String searchText) {
        String query = searchText.trim().toLowerCase();

        List<ClassSession> filteredSessions =
                CRMSContext.getRoutineManager()
                        .getSchedule()
                        .getSessions()
                        .stream()
                        .filter(session ->
                                session.getDay().toLowerCase()
                                        .contains(query)
                                        || session.getSubject()
                                        .toLowerCase()
                                        .contains(query)
                                        || session.getTeacher()
                                        .toLowerCase()
                                        .contains(query)
                        )
                        .toList();

        ObservableList<ClassSession> sessions =
                FXCollections.observableArrayList(filteredSessions);

        routineTable.setItems(sessions);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("CRMS Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);

        alert.showAndWait();
    }
}