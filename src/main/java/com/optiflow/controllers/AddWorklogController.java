package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.services.WorkLogService;
import com.optiflow.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    private VBox taskSection;

    @FXML
    private Label liveDataInfoLabel;

    @FXML
    private TextField hoursField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private WorkLogService workLogService;
    private TaskService taskService;
    private EmployeeService employeeService;
    private Employee currentEmployee;
    private List<Tasks> tasks;
    private boolean managerMode;

    @FXML
    public void initialize() {
        this.workLogService = new WorkLogService();
        this.taskService = new TaskService();
        this.employeeService = new EmployeeService();

        // Set today's date as default
        datePicker.setValue(LocalDate.now());

        User user = SessionManager.getUser();
        managerMode = user != null && user.isManager();

        if (managerMode) {
            if (taskSection != null) {
                taskSection.setVisible(false);
                taskSection.setManaged(false);
            }
            if (liveDataInfoLabel != null) {
                liveDataInfoLabel.setText("Manager mode: add a generalized worklog entry without task mapping.");
            }
        } else {
            loadTasks();
        }
    }

    private void loadTasks() {
        try {
            User user = SessionManager.getUser();
            if (user != null) {
                currentEmployee = employeeService.getEmployeeByUserId(user.getUserId());
            }

            if (currentEmployee != null) {
                tasks = taskService.getTaskByEmp(currentEmployee.getEmp_id());
            } else {
                tasks = taskService.getAllTasks();
            }

            ObservableList<String> taskNames = FXCollections.observableArrayList();
            if (tasks != null) {
                for (Tasks task : tasks) {
                    taskNames.add("T-" + task.getTask_id() + " | " + task.getTitle());
                }
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

        if (!managerMode && (taskComboBox.getValue() == null || taskComboBox.getValue().isEmpty())) {
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

        int taskId = 0;
        if (!managerMode) {
            String selectedTask = taskComboBox.getValue();
            taskId = Integer.parseInt(selectedTask.split(" ")[0].substring(2));
        }

        // Get description
        String description = descriptionArea.getText().trim();

        // Save worklog
        try {
            int employeeId = currentEmployee != null ? currentEmployee.getEmp_id() : resolveCurrentEmployeeId();
            if (employeeId <= 0) {
                showAlert("Error", "Unable to resolve the current employee.");
                return;
            }

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

    private int resolveCurrentEmployeeId() {
        try {
            User user = SessionManager.getUser();
            if (user == null) {
                return -1;
            }

            Employee employee = employeeService.getEmployeeByUserId(user.getUserId());
            return employee == null ? -1 : employee.getEmp_id();
        } catch (Exception ignored) {
            return -1;
        }
    }
}
