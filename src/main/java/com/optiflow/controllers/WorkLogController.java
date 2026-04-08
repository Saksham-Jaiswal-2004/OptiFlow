package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.models.WorkLog;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.services.WorkLogService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorkLogController {

    @FXML
    private Button addEntryBtn;

    @FXML
    private Label todayHoursLabel;

    @FXML
    private Label weeklyHoursLabel;

    @FXML
    private Label weeklyTargetLabel;

    @FXML
    private Label utilizationLabel;

    @FXML
    private Label lastSyncedLabel;

    @FXML
    private VBox timelineContainer;

    private final WorkLogService workLogService = new WorkLogService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    public void initialize() {
        addEntryBtn.setOnAction(e -> handleAddEntry());
        refreshWorklogsView();
    }

    private void handleAddEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addWorklogs.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Worklog Entry");
            Scene scene = new Scene(root, 620, 760);
            scene.setFill(Color.web("#05020a"));
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            Stage mainStage = (Stage) addEntryBtn.getScene().getWindow();
            dialogStage.initOwner(mainStage);
            dialogStage.showAndWait();

            refreshWorklogsView();

        } catch (Exception e) {
            showAlert("Error", "Failed to open add worklog dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshWorklogsView() {
        List<WorkLog> logs = new ArrayList<>();
        Employee currentEmployee = resolveCurrentEmployee();
        int weeklyCapacity = 0;

        try {
            if (currentEmployee != null) {
                weeklyCapacity = Math.max(currentEmployee.getWeeklyCapacity(), 0);
                List<WorkLog> fetched = workLogService.getEmployeeLogs(currentEmployee.getEmp_id());
                if (fetched != null) {
                    logs.addAll(fetched);
                }
            }
        } catch (Exception ignored) {
        }

        updateSummary(logs, weeklyCapacity);
        updateTimeline(logs);

        if (lastSyncedLabel != null) {
            lastSyncedLabel.setText("Last synced " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private void updateSummary(List<WorkLog> logs, int weeklyCapacity) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        int todayHours = 0;
        int weeklyHours = 0;

        for (WorkLog log : logs) {
            if (log.getWorkDate() == null) {
                continue;
            }

            LocalDate workDate = log.getWorkDate().toLocalDate();
            if (workDate.equals(today)) {
                todayHours += log.getHoursWorked();
            }

            if (!workDate.isBefore(weekStart) && !workDate.isAfter(weekEnd)) {
                weeklyHours += log.getHoursWorked();
            }
        }

        if (todayHoursLabel != null) {
            todayHoursLabel.setText(todayHours + "h");
        }

        if (weeklyHoursLabel != null) {
            weeklyHoursLabel.setText(weeklyHours + "h");
        }

        if (weeklyTargetLabel != null) {
            weeklyTargetLabel.setText(weeklyCapacity > 0 ? "Target: " + weeklyCapacity + "h" : "Target: --");
        }

        if (utilizationLabel != null) {
            int utilization = weeklyCapacity <= 0 ? 0 : (int) Math.round((weeklyHours * 100.0) / weeklyCapacity);
            utilizationLabel.setText(utilization + "%");
        }
    }

    private void updateTimeline(List<WorkLog> logs) {
        if (timelineContainer == null) {
            return;
        }

        timelineContainer.getChildren().clear();
        logs.sort(Comparator.comparing(WorkLog::getWorkDate, Comparator.nullsLast(Comparator.reverseOrder())));

        if (logs.isEmpty()) {
            Label empty = new Label("No worklog entries available yet.");
            empty.getStyleClass().add("dash-card-sub");
            timelineContainer.getChildren().add(empty);
            return;
        }

        for (WorkLog log : logs) {
            timelineContainer.getChildren().add(buildTimelineEntry(log));
        }
    }

    private VBox buildTimelineEntry(WorkLog log) {
        VBox card = new VBox(5);
        card.getStyleClass().add("panel-thread-parent");

        HBox top = new HBox(8);
        Label dateLabel = new Label(log.getWorkDate() == null ? "-" : log.getWorkDate().toLocalDate().format(DATE_FMT));
        dateLabel.getStyleClass().add("dash-card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label taskLabel = new Label(resolveTaskLabel(log.getTaskId()));
        taskLabel.getStyleClass().add("proj-page-text");
        top.getChildren().addAll(dateLabel, spacer, taskLabel);

        Label descriptionLabel = new Label(safeText(log.getDescription()));
        descriptionLabel.getStyleClass().add("dash-card-sub");
        descriptionLabel.setWrapText(true);

        Label hoursBadge = new Label(log.getHoursWorked() + "h");
        hoursBadge.getStyleClass().addAll("proj-status-badge", log.getHoursWorked() >= 6 ? "proj-status-good" : "proj-status-warn");

        card.getChildren().addAll(top, descriptionLabel, hoursBadge);
        return card;
    }

    private String resolveTaskLabel(int taskId) {
        if (taskId <= 0) {
            return "General Worklog";
        }
        try {
            Tasks task = taskService.getTaskById(taskId);
            if (task == null || task.getTitle() == null || task.getTitle().isBlank()) {
                return "Task #T-" + taskId;
            }
            return "Task #T-" + taskId + " - " + task.getTitle();
        } catch (Exception ignored) {
            return "Task #T-" + taskId;
        }
    }

    private Employee resolveCurrentEmployee() {
        try {
            User user = SessionManager.getUser();
            if (user == null) {
                return null;
            }
            return employeeService.getEmployeeByUserId(user.getUserId());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "No description provided." : value;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}