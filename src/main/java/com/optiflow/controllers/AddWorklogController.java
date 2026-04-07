package com.optiflow.controllers;

import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Tasks;
import com.optiflow.services.WorkLogService;
import com.optiflow.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AddWorklogController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> taskComboBox;

    @FXML
    private TextField hoursField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private WorkLogService workLogService;
    private TaskDAO taskDAO;
    private List<Tasks> tasks;

    @FXML
    public void initialize() {
        this.workLogService = new WorkLogService();
        this.taskDAO = new TaskDAO();

        // Set today's date as default
        datePicker.setValue(LocalDate.now());

        // Load tasks for the ComboBox
        loadTasks();

        // Set button handlers
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
    }

    private void loadTasks() {
        try {
            tasks = taskDAO.getAllTasks();
            ObservableList<String> taskNames = FXCollections.observableArrayList();
            for (Tasks task : tasks) {
                taskNames.add("T-" + task.getTask_id() + " | " + task.getTitle());
            }
            taskComboBox.setItems(taskNames);
        } catch (Exception e) {
            showAlert("Error", "Failed to load tasks: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        // Validate inputs
        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a date.");
            return;
        }

        if (taskComboBox.getValue() == null || taskComboBox.getValue().isEmpty()) {
            showAlert("Validation Error", "Please select a task.");
            return;
        }

        if (hoursField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter hours worked.");
            return;
        }

        // Parse hours
        double hours;
        try {
            hours = Double.parseDouble(hoursField.getText());
            if (hours <= 0) {
                showAlert("Validation Error", "Hours must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Hours must be a valid number.");
            return;
        }

        // Get selected task ID
        String selectedTask = taskComboBox.getValue();
        int taskId = Integer.parseInt(selectedTask.split(" ")[0].substring(2));

        // Get description
        String description = descriptionArea.getText().trim();

        // Save worklog
        try {
            int employeeId = SessionManager.getUser().getUserId();
            Date workDate = Date.valueOf(datePicker.getValue());

            boolean success = workLogService.logWork(employeeId, taskId, workDate, (int) hours, description);

            if (success) {
                showAlert("Success", "Worklog entry saved successfully!");
                handleCancel();
            } else {
                showAlert("Error", "Failed to save worklog. Please try again.");
            }
        } catch (Exception e) {
            showAlert("Error", "Error saving worklog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
