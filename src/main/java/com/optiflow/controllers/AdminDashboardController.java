package com.optiflow.controllers;

import com.optiflow.models.AuditLog;
import com.optiflow.models.Employee;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.services.AuditLogService;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.services.TaskService;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AdminDashboardController {

    private final ProjectService projectService = new ProjectService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();
    private final AuditLogService auditLogService = new AuditLogService();

    @FXML
    private VBox root;

    @FXML
    private Label projectsValue;

    @FXML
    private Label employeesValue;

    @FXML
    private Label managersValue;

    @FXML
    private Label tasksValue;

    @FXML
    private TableView<ProjectRow> projectTable;

    @FXML
    private Button addProjectBtn;

    @FXML
    private TableColumn<ProjectRow, String> projectNameColumn;

    @FXML
    private TableColumn<ProjectRow, String> managerColumn;

    @FXML
    private TableColumn<ProjectRow, Number> progressColumn;

    @FXML
    private TableColumn<ProjectRow, String> statusColumn;

    @FXML
    private TableView<AuditLogRow> auditLogTable;

    @FXML
    private TableColumn<AuditLogRow, String> auditDateColumn;

    @FXML
    private TableColumn<AuditLogRow, Number> auditUserColumn;

    @FXML
    private TableColumn<AuditLogRow, String> auditRoleColumn;

    @FXML
    private TableColumn<AuditLogRow, String> auditActionColumn;

    @FXML
    private TableColumn<AuditLogRow, String> auditEntityColumn;

    @FXML
    private TableColumn<AuditLogRow, String> auditDetailsColumn;

    @FXML
    private PieChart utilizationChart;

    @FXML
    private LineChart<String, Number> completionLineChart;

    @FXML
    private BarChart<String, Number> performanceBarChart;

    @FXML
    public void initialize() {
        ObservableList<ProjectRow> projects = loadProjectsFromServices();

        configureKpiCounters(projects);
        configureProjectOverview(projects);
        configureAuditLogs();
        configureUtilizationChart(projects);
        configureAnalytics(projects);

        animateCardsOnLoad();
        completionLineChart.setAnimated(true);
        performanceBarChart.setAnimated(true);
        utilizationChart.setAnimated(true);
    }

    private void configureKpiCounters(ObservableList<ProjectRow> projects) {
        int projectCount = projects.size();
        int employeeCount = 0;
        int managerCount = 0;
        int taskCount = 0;

        try {
            List<Employee> employees = employeeService.getAllEmployees();
            employeeCount = employees == null ? 0 : employees.size();
        } catch (Exception ignored) {
        }

        try {
            List<Employee> managers = employeeService.getAllManagers();
            managerCount = managers == null ? 0 : managers.size();
        } catch (Exception ignored) {
        }

        try {
            List<Projects> allProjects = projectService.getAllProjects();
            if (allProjects != null) {
                for (Projects project : allProjects) {
                    List<Tasks> projectTasks = taskService.getTaskByProject(project.getProject_id());
                    taskCount += projectTasks == null ? 0 : projectTasks.size();
                }
            }
        } catch (Exception ignored) {
        }

        animateCounter(projectsValue, projectCount);
        animateCounter(employeesValue, employeeCount);
        animateCounter(managersValue, managerCount);
        animateCounter(tasksValue, taskCount);
    }

    private ObservableList<ProjectRow> loadProjectsFromServices() {
        ObservableList<ProjectRow> rows = FXCollections.observableArrayList();

        try {
            List<Projects> projects = projectService.getAllProjects();
            if (projects == null) {
                return rows;
            }

            for (Projects project : projects) {
                String managerName = "Unassigned";
                try {
                    Employee manager = employeeService.getEmployeeById(project.getManager_id());
                    if (manager != null && manager.getName() != null) {
                        managerName = manager.getName();
                    }
                } catch (Exception ignored) {
                }

                int progress = 0;
                try {
                    progress = (int) Math.round(projectService.calculateProjectProgress(project.getProject_id()));
                    progress = Math.max(0, Math.min(100, progress));
                } catch (Exception ignored) {
                }

                rows.add(new ProjectRow(project.getName(), managerName, progress, normalizeProjectStatus(project.getStatus())));
            }
        } catch (Exception ignored) {
        }

        return rows;
    }

    private String normalizeProjectStatus(String status) {
        if (status == null) {
            return "At Risk";
        }
        String s = status.trim().toLowerCase(Locale.ROOT);
        if ("completed".equals(s) || "on track".equals(s) || "active".equals(s)) {
            return "On Track";
        }
        if ("at risk".equals(s) || "delayed".equals(s)) {
            return "At Risk";
        }
        return "Delayed";
    }

    private void configureProjectOverview(ObservableList<ProjectRow> rows) {
        projectNameColumn.setCellValueFactory(data -> data.getValue().projectProperty());
        managerColumn.setCellValueFactory(data -> data.getValue().managerProperty());
        progressColumn.setCellValueFactory(data -> data.getValue().progressProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        projectTable.setRowFactory(tv -> {
            TableRow<ProjectRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    openProjectDetail(row.getItem());
                }
            });
            return row;
        });

        progressColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                double value = item.doubleValue() / 100.0;
                ProgressBar bar = new ProgressBar(value);
                bar.getStyleClass().add("adm-progress");
                bar.setPrefWidth(120);

                Label pct = new Label((int) item.doubleValue() + "%");
                pct.getStyleClass().add("adm-progress-text");

                HBox wrap = new HBox(8, bar, pct);
                setGraphic(wrap);
                setText(null);
            }
        });

        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(item);
                badge.getStyleClass().add("adm-status-badge");
                if ("On Track".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("adm-status-good");
                } else if ("At Risk".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("adm-status-warn");
                } else {
                    badge.getStyleClass().add("adm-status-bad");
                }
                setGraphic(badge);
                setText(null);
            }
        });

        projectTable.setItems(rows);
    }

    private void configureAuditLogs() {
        auditDateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        auditUserColumn.setCellValueFactory(data -> data.getValue().userIdProperty());
        auditRoleColumn.setCellValueFactory(data -> data.getValue().roleProperty());
        auditActionColumn.setCellValueFactory(data -> data.getValue().actionProperty());
        auditEntityColumn.setCellValueFactory(data -> data.getValue().entityProperty());
        auditDetailsColumn.setCellValueFactory(data -> data.getValue().detailsProperty());

        ObservableList<AuditLogRow> rows = FXCollections.observableArrayList();

        try {
            List<AuditLog> logs = auditLogService.getAllLogs();
            if (logs != null) {
                logs.sort(
                        Comparator.comparing(AuditLog::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                                .reversed()
                                .thenComparing(AuditLog::getLog_id, Comparator.reverseOrder())
                );

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
                for (AuditLog log : logs) {
                    String date = log.getDate() == null ? "-" : log.getDate().toLocalDate().format(dtf);
                    String role = (log.getUser_role() == null || log.getUser_role().isBlank()) ? "-" : log.getUser_role();
                    String action = log.getAction() == null ? "-" : log.getAction();
                    String entity = (log.getEntityType() == null ? "-" : log.getEntityType()) + " #" + log.getEntity_id();
                    String details = log.getDetails() == null ? "-" : log.getDetails();

                    rows.add(new AuditLogRow(date, log.getUser_id(), role, action, entity, details));
                }
            }
        } catch (Exception ignored) {
        }

        auditLogTable.setItems(rows);
    }

    @FXML
    private void handleAddProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ProjectForm.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Project");
            dialogStage.setScene(new Scene(root, 760, 640));
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            Stage owner = (Stage) addProjectBtn.getScene().getWindow();
            dialogStage.initOwner(owner);
            dialogStage.showAndWait();

            ObservableList<ProjectRow> refreshed = loadProjectsFromServices();
            configureKpiCounters(refreshed);
            configureProjectOverview(refreshed);
            configureAuditLogs();
            configureUtilizationChart(refreshed);
            configureAnalytics(refreshed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openProjectDetail(ProjectRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ProjectDetail.fxml"));
            Parent root = loader.load();

            Stage detailStage = new Stage();
            detailStage.setTitle("Project Detail - " + row.projectProperty().get());
            detailStage.setScene(new Scene(root, 900, 650));
            detailStage.initModality(Modality.APPLICATION_MODAL);

            Stage owner = (Stage) projectTable.getScene().getWindow();
            detailStage.initOwner(owner);
            detailStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureUtilizationChart(ObservableList<ProjectRow> projects) {
        int onTrack = 0;
        int atRisk = 0;
        int delayed = 0;

        for (ProjectRow project : projects) {
            String status = project.getStatus();
            if ("On Track".equalsIgnoreCase(status)) {
                onTrack++;
            } else if ("At Risk".equalsIgnoreCase(status)) {
                atRisk++;
            } else {
                delayed++;
            }
        }

        if (projects.isEmpty()) {
            utilizationChart.setData(FXCollections.observableArrayList(new PieChart.Data("No Data", 1)));
            return;
        }

        utilizationChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("On Track", Math.max(onTrack, 0)),
                new PieChart.Data("At Risk", Math.max(atRisk, 0)),
                new PieChart.Data("Delayed", Math.max(delayed, 0))
        ));
    }

    private void configureAnalytics(ObservableList<ProjectRow> projects) {
        LineChart.Series<String, Number> completionSeries = new LineChart.Series<>();
        completionSeries.setName("Project Progress");

        BarChart.Series<String, Number> taskVolumeSeries = new BarChart.Series<>();
        taskVolumeSeries.setName("Tasks per Project");

        if (projects.isEmpty()) {
            completionSeries.getData().add(new LineChart.Data<>("No Data", 0));
            taskVolumeSeries.getData().add(new BarChart.Data<>("No Data", 0));
        } else {
            List<Projects> allProjects;
            try {
                allProjects = projectService.getAllProjects();
            } catch (Exception e) {
                allProjects = new ArrayList<>();
            }

            int limit = Math.min(8, projects.size());
            for (int i = 0; i < limit; i++) {
                ProjectRow row = projects.get(i);
                String label = truncate(row.getProject(), 14);
                completionSeries.getData().add(new LineChart.Data<>(label, row.getProgress()));

                int taskCount = 0;
                for (Projects project : allProjects) {
                    if (project.getName() != null && project.getName().equals(row.getProject())) {
                        try {
                            List<Tasks> tasks = taskService.getTaskByProject(project.getProject_id());
                            taskCount = tasks == null ? 0 : tasks.size();
                        } catch (Exception ignored) {
                        }
                        break;
                    }
                }
                taskVolumeSeries.getData().add(new BarChart.Data<>(label, taskCount));
            }
        }

        completionLineChart.getData().setAll(completionSeries);
        performanceBarChart.getData().setAll(taskVolumeSeries);
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return "-";
        }
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 1) + "…";
    }

    private void animateCounter(Label label, int target) {
        IntegerProperty value = new SimpleIntegerProperty(0);
        value.addListener((obs, oldVal, newVal) -> label.setText(String.valueOf(newVal.intValue())));

        Timeline timeline = new Timeline();
        int step = Math.max(1, Math.max(target / 24, 1));
        for (int current = 0; current <= target; current += step) {
            int snapshot = Math.min(current, target);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis((snapshot / (double) Math.max(1, target)) * 800), e -> value.set(snapshot))
            );
        }
        timeline.play();
    }

    private void animateCardsOnLoad() {
        if (root == null) {
            return;
        }

        int i = 0;
        for (Node node : root.lookupAll(".adm-card, .adm-kpi-card")) {
            node.setOpacity(0);
            node.setScaleX(0.985);
            node.setScaleY(0.985);

            FadeTransition fade = new FadeTransition(Duration.millis(320), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(i * 60L));

            ScaleTransition scale = new ScaleTransition(Duration.millis(320), node);
            scale.setFromX(0.985);
            scale.setFromY(0.985);
            scale.setToX(1);
            scale.setToY(1);
            scale.setDelay(Duration.millis(i * 60L));

            fade.play();
            scale.play();
            i++;
        }
    }

    public static class ProjectRow {
        private final StringProperty project;
        private final StringProperty manager;
        private final IntegerProperty progress;
        private final StringProperty status;

        public ProjectRow(String project, String manager, int progress, String status) {
            this.project = new SimpleStringProperty(project);
            this.manager = new SimpleStringProperty(manager);
            this.progress = new SimpleIntegerProperty(progress);
            this.status = new SimpleStringProperty(status);
        }

        public String getProject() {
            return project.get();
        }

        public String getStatus() {
            return status.get();
        }

        public int getProgress() {
            return progress.get();
        }

        public StringProperty projectProperty() {
            return project;
        }

        public StringProperty managerProperty() {
            return manager;
        }

        public IntegerProperty progressProperty() {
            return progress;
        }

        public StringProperty statusProperty() {
            return status;
        }
    }

    public static class AuditLogRow {
        private final StringProperty date;
        private final IntegerProperty userId;
        private final StringProperty role;
        private final StringProperty action;
        private final StringProperty entity;
        private final StringProperty details;

        public AuditLogRow(String date, int userId, String role, String action, String entity, String details) {
            this.date = new SimpleStringProperty(date);
            this.userId = new SimpleIntegerProperty(userId);
            this.role = new SimpleStringProperty(role);
            this.action = new SimpleStringProperty(action);
            this.entity = new SimpleStringProperty(entity);
            this.details = new SimpleStringProperty(details);
        }

        public StringProperty dateProperty() {
            return date;
        }

        public IntegerProperty userIdProperty() {
            return userId;
        }

        public StringProperty roleProperty() {
            return role;
        }

        public StringProperty actionProperty() {
            return action;
        }

        public StringProperty entityProperty() {
            return entity;
        }

        public StringProperty detailsProperty() {
            return details;
        }
    }
}
