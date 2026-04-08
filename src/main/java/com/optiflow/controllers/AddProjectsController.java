package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProjectsController {

    @FXML private TextField projectNameField;
    @FXML private CheckBox frontendCheck;
    @FXML private CheckBox backendCheck;
    @FXML private CheckBox aiCheck;
    @FXML private CheckBox blockchainCheck;
    @FXML private ComboBox<String> managerDropdown;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextArea descriptionArea;

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final Map<String, Integer> managerIds = new HashMap<>();

    @FXML
    public void initialize() {
        if (deadlinePicker != null) {
            deadlinePicker.setValue(LocalDate.now().plusWeeks(4));
        }
        loadManagers();
        autoSelectCurrentManager();
    }

    @FXML
    private void handleCreateProject() {
        String name = safeText(projectNameField == null ? null : projectNameField.getText());
        String description = safeText(descriptionArea == null ? null : descriptionArea.getText());

        if (name.isBlank() || description.isBlank() || deadlinePicker == null || deadlinePicker.getValue() == null) {
            showAlert("Validation Error", "Project name, description, and deadline are required.");
            return;
        }

        try {
            int selectedManagerId = resolveSelectedManagerId();
            if (selectedManagerId <= 0) {
                showAlert("Validation Error", "Please select an available manager.");
                return;
            }

            if (!projectService.isManagerAvailableForNewProject(selectedManagerId)) {
                showAlert("Manager Busy", "This manager already has an active project assigned.");
                loadManagers();
                return;
            }

            Projects project = new Projects();
            project.setName(name);
            project.setDescription(buildDescription(description));
            project.setStart_date(Date.valueOf(LocalDate.now()));
            project.setEnd_date(Date.valueOf(deadlinePicker.getValue()));
            project.setDeadline(Date.valueOf(deadlinePicker.getValue()));
            project.setManager_id(selectedManagerId);
            project.setStatus("PLANNED");

            if (!projectService.createProject(project)) {
                showAlert("Save Failed", "The project could not be saved.");
                return;
            }

            showAlert("Success", "Project created successfully.");
            resetForm();
        } catch (Exception e) {
            showAlert("Error", "Failed to create project: " + e.getMessage());
        }
    }

    private void loadManagers() {
        if (managerDropdown == null) {
            return;
        }

        managerDropdown.getItems().clear();
        managerIds.clear();

        try {
            List<Employee> managers = employeeService.getAllManagers();
            if (managers != null) {
                for (Employee manager : managers) {
                    if (!projectService.isManagerAvailableForNewProject(manager.getEmp_id())) {
                        continue;
                    }
                    String label = manager.getName() + " (#" + manager.getEmp_id() + ")";
                    managerIds.put(label, manager.getEmp_id());
                    managerDropdown.getItems().add(label);
                }
            }
        } catch (Exception ignored) {
        }

        if (!managerDropdown.getItems().isEmpty()) {
            managerDropdown.getSelectionModel().selectFirst();
        } else {
            managerDropdown.setPromptText("No available managers");
        }
    }

    private void autoSelectCurrentManager() {
        try {
            if (SessionManager.getUser() == null || managerDropdown == null) {
                return;
            }

            List<Employee> managers = employeeService.getAllManagers();
            if (managers == null) {
                return;
            }

            for (Employee manager : managers) {
                if (manager.getUser_id() == SessionManager.getUser().getUserId()) {
                    String label = manager.getName() + " (#" + manager.getEmp_id() + ")";
                    managerDropdown.getSelectionModel().select(label);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private int resolveSelectedManagerId() {
        if (managerDropdown == null || managerDropdown.getValue() == null) {
            return 0;
        }
        return managerIds.getOrDefault(managerDropdown.getValue(), 0);
    }

    private String buildDescription(String description) {
        StringBuilder builder = new StringBuilder(description.trim());
        String skills = collectSkills();
        if (!skills.isBlank()) {
            builder.append(System.lineSeparator()).append("Required skills: ").append(skills);
        }
        return builder.toString();
    }

    private String collectSkills() {
        StringBuilder builder = new StringBuilder();
        appendSkill(builder, frontendCheck, "Frontend");
        appendSkill(builder, backendCheck, "Backend");
        appendSkill(builder, aiCheck, "AI/ML");
        appendSkill(builder, blockchainCheck, "Blockchain");
        return builder.toString();
    }

    private void appendSkill(StringBuilder builder, CheckBox checkBox, String skill) {
        if (checkBox != null && checkBox.isSelected()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(skill);
        }
    }

    private void resetForm() {
        if (projectNameField != null) {
            projectNameField.clear();
        }
        if (descriptionArea != null) {
            descriptionArea.clear();
        }
        if (frontendCheck != null) frontendCheck.setSelected(false);
        if (backendCheck != null) backendCheck.setSelected(false);
        if (aiCheck != null) aiCheck.setSelected(false);
        if (blockchainCheck != null) blockchainCheck.setSelected(false);
        if (deadlinePicker != null) {
            deadlinePicker.setValue(LocalDate.now().plusWeeks(4));
        }
        autoSelectCurrentManager();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}