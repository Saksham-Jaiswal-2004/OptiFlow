package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProjectDetailController {

    @FXML private VBox detailCard;
    @FXML private Label projectNameLabel;
    @FXML private Label projectIdLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private Label statusLabel;
    @FXML private Label managerLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label taskCountLabel;
    @FXML private VBox taskListContainer;

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        loadProjectDetails();
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
            if (projectIdLabel != null) {
                projectIdLabel.setText("Project ID: " + project.getProject_id());
            }
            if (projectDescriptionLabel != null) {
                String description = project.getDescription() == null || project.getDescription().isBlank()
                        ? "No project description available."
                        : project.getDescription();
                projectDescriptionLabel.setText(description);
            }
            if (statusLabel != null) {
                String normalizedStatus = normalizeProjectStatus(project.getStatus());
                statusLabel.setText(normalizedStatus);
                statusLabel.getStyleClass().removeAll("mgr-status-done", "mgr-status-progress", "mgr-status-pending");
                if ("Completed".equalsIgnoreCase(normalizedStatus)) {
                    statusLabel.getStyleClass().add("mgr-status-done");
                } else if ("In Progress".equalsIgnoreCase(normalizedStatus) || "On Track".equalsIgnoreCase(normalizedStatus)) {
                    statusLabel.getStyleClass().add("mgr-status-progress");
                } else {
                    statusLabel.getStyleClass().add("mgr-status-pending");
                }
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

        HBox top = new HBox(8);
        Label title = new Label(task.getTitle() == null || task.getTitle().isBlank() ? "Untitled Task" : task.getTitle());
        title.getStyleClass().add("dash-card-sub");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(task.getStatus() == null ? "Pending" : task.getStatus());
        status.getStyleClass().addAll("mgr-status-badge", "mgr-status-progress");

        top.getChildren().addAll(title, spacer, status);

        String due = task.getEnd_date() == null ? "-" : task.getEnd_date().toString();
        String assignee = resolveAssignee(task.getAssigned_to());
        Label meta = new Label("Assignee: " + assignee + "   |   Due: " + due);
        meta.getStyleClass().add("proj-page-text");

        row.getChildren().addAll(top, meta);
        return row;
    }

    private String resolveAssignee(int assigneeId) {
        try {
            Employee employee = employeeService.getEmployeeById(assigneeId);
            if (employee != null && employee.getName() != null && !employee.getName().isBlank()) {
                return employee.getName();
            }
            Employee byUser = employeeService.getEmployeeByUserId(assigneeId);
            if (byUser != null && byUser.getName() != null && !byUser.getName().isBlank()) {
                return byUser.getName();
            }
        } catch (Exception ignored) {
        }
        return "Unassigned";
    }

    private String normalizeProjectStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Pending";
        }
        String normalized = status.trim().toLowerCase();
        if (normalized.contains("complete")) {
            return "Completed";
        }
        if (normalized.contains("progress") || normalized.contains("active") || normalized.contains("track")) {
            return "In Progress";
        }
        return "Delayed";
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
