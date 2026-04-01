package com.optiflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WorkLogController {

    @FXML
    private TextField employeeIdField;

    @FXML
    private TextField taskNameField;

    @FXML
    private TextField hoursField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private void handleSubmitWorklog() {

        String empId = employeeIdField.getText();
        String taskName = taskNameField.getText();
        String hoursText = hoursField.getText();
        String description = descriptionArea.getText();


        if (empId.isEmpty() || taskName.isEmpty() || hoursText.isEmpty()) {
            showAlert("Error", "Please fill all required fields!");
            return;
        }

        double hours;

        try {
            hours = Double.parseDouble(hoursText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Hours must be a number!");
            return;
        }


        System.out.println("Worklog Added:");
        System.out.println("Employee ID: " + empId);
        System.out.println("Task: " + taskName);
        System.out.println("Hours: " + hours);
        System.out.println("Description: " + description);

        showAlert("Success", "Worklog submitted successfully!");

        clearFields();
    }

    private void clearFields() {
        employeeIdField.clear();
        taskNameField.clear();
        hoursField.clear();
        descriptionArea.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}