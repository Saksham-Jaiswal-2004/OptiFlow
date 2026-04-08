package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.utils.SessionManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Locale;

public class ManagerDetailController {

    @FXML private Button editManagerBtn;
    @FXML private Label managerNameLabel;
    @FXML private Label departmentLabel;
    @FXML private Label emailLabel;
    @FXML private Label statusLabel;
    @FXML private Label workloadLabel;
    @FXML private ProgressBar workloadProgressBar;
    @FXML private Label activeProjectsLabel;
    @FXML private Label completedProjectsLabel;
    @FXML private Label totalProjectsLabel;
    @FXML private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> projectNameColumn;
    @FXML private TableColumn<ProjectRow, String> projectStatusColumn;
    @FXML private TableColumn<ProjectRow, Number> projectProgressColumn;
    @FXML private TableColumn<ProjectRow, String> projectDeadlineColumn;
    @FXML private TableColumn<ProjectRow, Number> projectTeamSizeColumn;

    private final EmployeeService employeeService = new EmployeeService();
    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        configureTable();
        loadManagerData();
    }

    @FXML
    private void handleEditManager() {
        if (editManagerBtn != null) {
            editManagerBtn.setText("Editing is not wired yet");
        }
    }

    private void loadManagerData() {
        try {
            Employee manager = resolveManager();
            if (manager == null) {
                return;
            }

            if (managerNameLabel != null) managerNameLabel.setText(manager.getName());
            if (departmentLabel != null) departmentLabel.setText(manager.getDepartment());
            if (emailLabel != null) emailLabel.setText("user-" + manager.getUser_id() + "@optiflow.local");
            if (statusLabel != null) statusLabel.setText(normalize(manager.getStatus()));

            List<Employee> team = employeeService.getEmployeesByManager(manager.getEmp_id());
            List<Projects> projects = projectService.getAllProjects();

            int totalProjects = projects == null ? 0 : projects.size();
            int completedProjects = 0;
            int activeProjects = 0;

            ObservableList<ProjectRow> rows = FXCollections.observableArrayList();
            if (projects != null) {
                for (Projects project : projects) {
                    if (project.getManager_id() != manager.getEmp_id()) {
                        continue;
                    }
                    String status = normalize(project.getStatus());
                    if ("Completed".equals(status)) {
                        completedProjects++;
                    } else {
                        activeProjects++;
                    }

                    int progress = (int) Math.round(projectService.calculateProjectProgress(project.getProject_id()));
                    rows.add(new ProjectRow(
                            project.getName(),
                            status,
                            progress,
                            project.getDeadline() == null ? "-" : project.getDeadline().toString(),
                            team == null ? 0 : team.size()
                    ));
                }
            }

            if (totalProjectsLabel != null) totalProjectsLabel.setText(String.valueOf(totalProjects));
            if (activeProjectsLabel != null) activeProjectsLabel.setText(String.valueOf(activeProjects));
            if (completedProjectsLabel != null) completedProjectsLabel.setText(String.valueOf(completedProjects));
            if (workloadLabel != null) workloadLabel.setText(Math.min(100, manager.getAllocated_hours()) + "%");
            if (workloadProgressBar != null) workloadProgressBar.setProgress(Math.min(1.0, manager.getAllocated_hours() / 100.0));

            if (projectTable != null) {
                projectTable.setItems(rows);
            }
        } catch (Exception ignored) {
        }
    }

    private void configureTable() {
        if (projectNameColumn != null) projectNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        if (projectStatusColumn != null) projectStatusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        if (projectProgressColumn != null) projectProgressColumn.setCellValueFactory(data -> data.getValue().progressProperty());
        if (projectDeadlineColumn != null) projectDeadlineColumn.setCellValueFactory(data -> data.getValue().deadlineProperty());
        if (projectTeamSizeColumn != null) projectTeamSizeColumn.setCellValueFactory(data -> data.getValue().teamSizeProperty());
    }

    private Employee resolveManager() throws Exception {
        if (SessionManager.getUser() != null) {
            Employee current = employeeService.getEmployeeByUserId(SessionManager.getUser().getUserId());
            if (current != null) {
                return current;
            }
        }

        List<Employee> managers = employeeService.getAllManagers();
        return managers == null || managers.isEmpty() ? null : managers.get(0);
    }

    private String normalize(String status) {
        if (status == null) {
            return "Active";
        }

        String normalized = status.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("complete")) {
            return "Completed";
        }
        if (normalized.contains("inactive")) {
            return "Inactive";
        }
        return "Active";
    }

    public static class ProjectRow {
        private final StringProperty name;
        private final StringProperty status;
        private final IntegerProperty progress;
        private final StringProperty deadline;
        private final IntegerProperty teamSize;

        public ProjectRow(String name, String status, int progress, String deadline, int teamSize) {
            this.name = new SimpleStringProperty(name);
            this.status = new SimpleStringProperty(status);
            this.progress = new SimpleIntegerProperty(progress);
            this.deadline = new SimpleStringProperty(deadline);
            this.teamSize = new SimpleIntegerProperty(teamSize);
        }

        public StringProperty nameProperty() { return name; }
        public StringProperty statusProperty() { return status; }
        public IntegerProperty progressProperty() { return progress; }
        public StringProperty deadlineProperty() { return deadline; }
        public IntegerProperty teamSizeProperty() { return teamSize; }
    }
}