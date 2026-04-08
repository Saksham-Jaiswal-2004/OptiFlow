package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class ViewProjectsController {

    @FXML private Button closeButton;
    @FXML private Label projectNameLabel;
    @FXML private Label managerNameField;
    @FXML private Label statusField;
    @FXML private Label managerIDField;
    @FXML private Label employeeIDField;
    @FXML private Label startField;
    @FXML private Label deadField;
    @FXML private Label projectStatusField;
    @FXML private TextArea descField;

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        loadProjectDetails();
    }

    @FXML
    private void handleClose() {
        if (closeButton != null && closeButton.getScene() != null) {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }

    private void loadProjectDetails() {
        try {
            Projects project = resolveProject();
            if (project == null) {
                return;
            }

            if (projectNameLabel != null) projectNameLabel.setText(project.getName());
            if (managerNameField != null) managerNameField.setText(resolveManagerName(project.getManager_id()));
            if (statusField != null) statusField.setText(project.getStatus());
            if (projectStatusField != null) projectStatusField.setText(project.getStatus());
            if (managerIDField != null) managerIDField.setText(String.valueOf(project.getManager_id()));
            if (startField != null) startField.setText(project.getStart_date() == null ? "-" : project.getStart_date().toString());
            if (deadField != null) deadField.setText(project.getDeadline() == null ? "-" : project.getDeadline().toString());
            if (descField != null) descField.setText(project.getDescription());

            List<Tasks> tasks = taskService.getTaskByProject(project.getProject_id());
            if (employeeIDField != null) {
                employeeIDField.setText(tasks == null ? "0" : String.valueOf(tasks.stream().filter(task -> task.getAssigned_to() > 0).count()));
            }
        } catch (Exception ignored) {
        }
    }

    private String resolveManagerName(int managerId) {
        try {
            Employee manager = employeeService.getEmployeeById(managerId);
            if (manager != null && manager.getName() != null) {
                return manager.getName();
            }
        } catch (Exception ignored) {
        }
        return "-";
    }

    private Projects resolveProject() throws Exception {
        if (SessionManager.getUser() != null) {
            Employee current = employeeService.getEmployeeByUserId(SessionManager.getUser().getUserId());
            if (current != null) {
                int projectId = projectService.getProjectByManager(current.getEmp_id());
                if (projectId > 0) {
                    Projects project = projectService.getProjectById(projectId);
                    if (project != null) {
                        return project;
                    }
                }
            }
        }

        List<Projects> projects = projectService.getAllProjects();
        return projects == null || projects.isEmpty() ? null : projects.get(0);
    }
}