package com.optiflow.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class ProjectFormController {

    @FXML
    private TextField projectNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> priorityCombo;

    @FXML
    private Label projectNameError;

    @FXML
    private Label descriptionError;

    @FXML
    private Label startDateError;

    @FXML
    private Label endDateError;

    @FXML
    private Label priorityError;

    @FXML
    private Label formSuccessMessage;

    @FXML
    public void initialize() {
        priorityCombo.setItems(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
    }

    @FXML
    private void handleGenerateTasks() {
        System.out.println("AI Generate Tasks clicked");
    }

    @FXML
    private void handleAddTaskManually() {
        System.out.println("Add Task Manually clicked");
    }

    @FXML
    private void handleSaveProject() {
        hideMessages();

        boolean valid = validateForm();
        if (!valid) {
            return;
        }

        formSuccessMessage.setVisible(true);
        formSuccessMessage.setManaged(true);
        System.out.println("Project saved successfully");
    }

    private boolean validateForm() {
        boolean valid = true;

        if (isBlank(projectNameField.getText())) {
            showError(projectNameError, "Project name is required");
            valid = false;
        }

        if (isBlank(descriptionField.getText())) {
            showError(descriptionError, "Description is required");
            valid = false;
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null) {
            showError(startDateError, "Start date is required");
            valid = false;
        }

        if (end == null) {
            showError(endDateError, "End date is required");
            valid = false;
        }

        if (start != null && end != null && end.isBefore(start)) {
            showError(endDateError, "End date must be after start date");
            valid = false;
        }

        if (priorityCombo.getValue() == null) {
            showError(priorityError, "Please select a priority");
            valid = false;
        }

        return valid;
    }

    private void hideMessages() {
        hideError(projectNameError);
        hideError(descriptionError);
        hideError(startDateError);
        hideError(endDateError);
        hideError(priorityError);

        formSuccessMessage.setVisible(false);
        formSuccessMessage.setManaged(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
