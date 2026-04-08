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
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProjectDetailController {

    @FXML private VBox detailCard;
    @FXML private Label projectNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label managerLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label taskCountLabel;
    @FXML private VBox taskListContainer;
    @FXML private Button assignManagerBtn;

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        loadProjectDetails();
    }

    @FXML
    private void handleAssignManager() {
        if (assignManagerBtn != null) {
            assignManagerBtn.setText("Assign Manager is not wired yet");
        }
    }

    private void loadProjectDetails() {
        try {
            Projects project = resolveProject();
            if (project == null) {
                return;
            }

            if (projectNameLabel != null) {
                projectNameLabel.setText(project.getName());
            }
            if (statusLabel != null) {
                statusLabel.setText(project.getStatus());
            }
            if (startDateLabel != null) {
                startDateLabel.setText(project.getStart_date() == null ? "-" : project.getStart_date().toString());
            }
            if (endDateLabel != null) {
                endDateLabel.setText(project.getDeadline() == null ? "-" : project.getDeadline().toString());
            }

            Employee manager = employeeService.getEmployeeById(project.getManager_id());
            if (managerLabel != null) {
                managerLabel.setText(manager == null ? "Unassigned" : manager.getName());
            }

            double progress = projectService.calculateProjectProgress(project.getProject_id());
            if (progressBar != null) {
                progressBar.setProgress(Math.max(0, Math.min(1.0, progress / 100.0)));
            }
            if (progressLabel != null) {
                progressLabel.setText((int) Math.round(progress) + "% completed");
            }

            List<Tasks> tasks = taskService.getTaskByProject(project.getProject_id());
            int taskCount = tasks == null ? 0 : tasks.size();
            if (taskCountLabel != null) {
                taskCountLabel.setText(taskCount + " tasks");
            }

            if (taskListContainer != null) {
                taskListContainer.getChildren().clear();
                if (tasks != null) {
                    for (Tasks task : tasks) {
                        taskListContainer.getChildren().add(createTaskRow(task));
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private VBox createTaskRow(Tasks task) {
        VBox row = new VBox(4);
        row.getStyleClass().add("emp-comment-row");

        Label title = new Label(task.getTitle());
        title.getStyleClass().add("dash-card-sub");
        Label status = new Label(task.getStatus() == null ? "Pending" : task.getStatus());
        status.getStyleClass().add("emp-status-badge");
        row.getChildren().addAll(title, status);
        return row;
    }

    private Projects resolveProject() throws Exception {
        if (SessionManager.getUser() != null) {
            Employee current = employeeService.getEmployeeByUserId(SessionManager.getUser().getUserId());
            if (current != null) {
                int projectId = projectService.getProjectByManager(current.getEmp_id());
                if (projectId > 0) {
                    return projectService.getProjectById(projectId);
                }
            }
        }

        List<Projects> projects = projectService.getAllProjects();
        return projects == null || projects.isEmpty() ? null : projects.get(0);
    }
}
