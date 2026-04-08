package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class ViewManagersController {

    @FXML private Button closeButton;
    @FXML private Label managerNameField;
    @FXML private Label projectNameField;
    @FXML private Label statusField;
    @FXML private Label managerIDField;
    @FXML private Label employeeIDField;
    @FXML private Label startField;
    @FXML private Label deadField;
    @FXML private TextArea descField;

    private final EmployeeService employeeService = new EmployeeService();
    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        loadManagerView();
    }

    @FXML
    private void handleClose() {
        if (closeButton != null && closeButton.getScene() != null) {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }

    private void loadManagerView() {
        try {
            Employee manager = resolveManager();
            if (manager == null) {
                return;
            }

            Projects project = resolveProject(manager.getEmp_id());

            if (managerNameField != null) managerNameField.setText(manager.getName());
            if (managerIDField != null) managerIDField.setText(String.valueOf(manager.getEmp_id()));
            if (employeeIDField != null) {
                List<Employee> team = employeeService.getEmployeesByManager(manager.getEmp_id());
                employeeIDField.setText(team == null ? "0" : String.valueOf(team.size()));
            }

            if (project != null) {
                if (projectNameField != null) projectNameField.setText(project.getName());
                if (statusField != null) statusField.setText(project.getStatus());
                if (startField != null) startField.setText(project.getStart_date() == null ? "-" : project.getStart_date().toString());
                if (deadField != null) deadField.setText(project.getDeadline() == null ? "-" : project.getDeadline().toString());
                if (descField != null) descField.setText(project.getDescription());
            }
        } catch (Exception ignored) {
        }
    }

    private Employee resolveManager() throws Exception {
        List<Employee> managers = employeeService.getAllManagers();
        return managers == null || managers.isEmpty() ? null : managers.get(0);
    }

    private Projects resolveProject(int managerId) throws Exception {
        int projectId = projectService.getProjectByManager(managerId);
        if (projectId > 0) {
            return projectService.getProjectById(projectId);
        }

        List<Projects> projects = projectService.getAllProjects();
        return projects == null || projects.isEmpty() ? null : projects.get(0);
    }
}