package com.optiflow.controllers;

import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.models.Employee;
import com.optiflow.services.ProjectService;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ReferenceDataService;
import com.optiflow.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public class ProjectFormController {

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final ReferenceDataService referenceDataService = new ReferenceDataService();

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
        priorityCombo.setItems(FXCollections.observableArrayList(referenceDataService.getTaskPriorities()));
    }

    @FXML
    private void handleGenerateTasks() {
        hideMessages();
        if (isBlank(projectNameField.getText()) || isBlank(descriptionField.getText())) {
            showError(projectNameError, "Project name is required before task generation");
            showError(descriptionError, "Description is required before task generation");
            return;
        }

        List<Tasks> generated = projectService.generateTasksForProjects(
                projectNameField.getText().trim(),
                descriptionField.getText().trim()
        );

        if (generated == null || generated.isEmpty()) {
            showError(formSuccessMessage, "Could not generate tasks at the moment.");
            return;
        }

        formSuccessMessage.setText("Generated " + generated.size() + " tasks from AI.");
        formSuccessMessage.setVisible(true);
        formSuccessMessage.setManaged(true);
    }

    @FXML
    private void handleAddTaskManually() {
        formSuccessMessage.setText("Manual task mode is available in the Tasks section.");
        formSuccessMessage.setVisible(true);
        formSuccessMessage.setManaged(true);
    }

    @FXML
    private void handleSaveProject() {
        hideMessages();

        boolean valid = validateForm();
        if (!valid) {
            return;
        }

        try {
            Projects project = new Projects();
            project.setName(projectNameField.getText().trim());
            project.setDescription(descriptionField.getText().trim());
            project.setStart_date(Date.valueOf(startDatePicker.getValue()));
            project.setEnd_date(Date.valueOf(endDatePicker.getValue()));
            project.setDeadline(Date.valueOf(endDatePicker.getValue()));
            project.setStatus(referenceDataService.getDefaultProjectStatus());
            project.setManager_id(resolveCurrentManagerId());

            boolean saved = projectService.createProject(project);
            if (!saved) {
                showError(formSuccessMessage, "Unable to save project. Check required data.");
                return;
            }

            formSuccessMessage.setText("Project saved successfully.");
            formSuccessMessage.setVisible(true);
            formSuccessMessage.setManaged(true);
        } catch (Exception e) {
            showError(formSuccessMessage, "Failed to save project.");
        }
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

    private int resolveCurrentManagerId() {
        try {
            if (SessionManager.getUser() == null) {
                return 0;
            }

            Employee manager = employeeService.getEmployeeByUserId(SessionManager.getUser().getUserId());
            return manager == null ? 0 : manager.getEmp_id();
        } catch (Exception ignored) {
            return 0;
        }
    }
}
