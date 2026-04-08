package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class ProjectsController {

    @FXML private VBox mainContainer;
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> projectFilterDropdown;
    @FXML private Button addProjectBtn;
    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private Button editProjectBtn;
    @FXML private Button openProjectBtn;

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();

    @FXML
    public void initialize() {
        configureHeader();
        configureFilters();
        loadProjects();
    }

    @FXML
    private void handleAddProject() {
        if (addProjectBtn != null) {
            addProjectBtn.setText("Open Project Form");
        }
    }

    @FXML
    private void handleDashboard() {
        navigateToDashboard();
    }

    @FXML
    private void handleManagers() {
        navigateToDashboard();
    }

    @FXML
    private void handleProjects() {
        navigateToDashboard();
    }

    @FXML
    private void handleEmployees() {
        navigateToDashboard();
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.setUser(null);
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception ignored) {
        }
    }

    private void configureHeader() {
        if (welcomeLabel == null) {
            return;
        }

        String name = SessionManager.getUser() == null || SessionManager.getUser().getName() == null
                ? "Projects"
                : SessionManager.getUser().getName() + "'s Projects";
        welcomeLabel.setText(name);
    }

    private void configureFilters() {
        if (projectFilterDropdown == null) {
            return;
        }

        projectFilterDropdown.getItems().clear();
        projectFilterDropdown.getItems().addAll("All", "Active", "Completed", "Delayed", "At Risk");
        projectFilterDropdown.getSelectionModel().selectFirst();
    }

    private void loadProjects() {
        try {
            List<Projects> projects = projectService.getAllProjects();
            if (projects == null || projects.isEmpty()) {
                setTemplateText("No projects available", "Create a project to start tracking delivery.");
                return;
            }

            Projects first = projects.get(0);
            setTemplateText(first.getName(), safeDescription(first.getDescription()));

            for (int index = 1; index < projects.size(); index++) {
                mainContainer.getChildren().add(createProjectCard(projects.get(index)));
            }
        } catch (Exception e) {
            setTemplateText("Unable to load projects", e.getMessage() == null ? "Service unavailable" : e.getMessage());
        }
    }

    private VBox createProjectCard(Projects project) {
        String status = normalize(project);
        String managerName = resolveManagerName(project.getManager_id());
        String deadline = project.getDeadline() == null ? "-" : project.getDeadline().toString();
        double progress = calculateProgress(project);

        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #374151; -fx-padding: 15; -fx-background-radius: 10; -fx-border-radius: 10;");

        HBox header = new HBox(10);
        Label title = new Label(project.getName());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label statusBadge = new Label(status);
        statusBadge.setStyle("-fx-background-color: linear-gradient(to right, #1E3A8A, #3B82F6); -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 10;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button edit = new Button("Edit");
        edit.setStyle("-fx-background-color: linear-gradient(to right, #1E3A8A, #3B82F6); -fx-text-fill: white;");
        header.getChildren().addAll(title, statusBadge, spacer, edit);

        Label description = new Label(safeDescription(project.getDescription()));
        description.setStyle("-fx-text-fill: #6B7280;");
        description.setWrapText(true);

        HBox meta = new HBox(18);
        Label manager = new Label("Manager: " + managerName);
        Label deadlineLabel = new Label("Deadline: " + deadline);
        Label progressLabel = new Label("Progress: " + (int) Math.round(progress) + "%");
        manager.setStyle("-fx-text-fill: white;");
        deadlineLabel.setStyle("-fx-text-fill: white;");
        progressLabel.setStyle("-fx-text-fill: white;");
        meta.getChildren().addAll(manager, deadlineLabel, progressLabel);

        Button open = new Button("Open Project");
        open.setStyle("-fx-background-color: linear-gradient(to right, #065F46, #10B981); -fx-text-fill: white;");

        card.getChildren().addAll(header, description, meta, open);
        return card;
    }

    private void setTemplateText(String title, String description) {
        if (projectNameLabel != null) {
            projectNameLabel.setText(title);
        }
        if (projectDescriptionLabel != null) {
            projectDescriptionLabel.setText(description);
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
        return "Unassigned";
    }

    private double calculateProgress(Projects project) {
        try {
            double progress = projectService.calculateProjectProgress(project.getProject_id());
            if (Double.isNaN(progress) || Double.isInfinite(progress)) {
                return 0;
            }
            return Math.max(0, Math.min(100, progress));
        } catch (Exception ignored) {
        }
        return 0;
    }

    private String normalize(Projects project) {
        if (project == null) {
            return "At Risk";
        }

        if (project.isCompleted()) {
            return "Completed";
        }

        if (project.getDeadline() == null) {
            return "At Risk";
        }

        LocalDate deadline = project.getDeadline().toLocalDate();
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        if (daysRemaining < 0) {
            return "Delayed";
        }
        if (daysRemaining <= 7) {
            return "At Risk";
        }
        return "Active";
    }

    private String safeDescription(String description) {
        return description == null || description.isBlank() ? "No project description available." : description;
    }

    private void navigateToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/DashboardLayout.fxml"));
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception ignored) {
        }
    }
}