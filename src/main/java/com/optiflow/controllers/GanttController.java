package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Orientation;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class GanttController {

    private static final int DAY_WIDTH = 80;
    private static final int EMP_WIDTH = 140;

    @FXML
    private VBox ganttContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        configureScopeLabels();
        loadEmployeeTimeline();
    }

    private void configureScopeLabels() {
        User user = SessionManager.getUser();
        if (titleLabel != null) {
            if (user != null && user.isEmployee()) {
                titleLabel.setText("My Weekly Workload");
            } else if (user != null && user.isManager()) {
                titleLabel.setText("Team Weekly Workload");
            } else {
                titleLabel.setText("Employee Weekly Workload");
            }
        }

        if (subtitleLabel != null) {
            subtitleLabel.setText("Live workload pulled from employee and task services.");
        }
    }

    private void loadEmployeeTimeline() {
        ganttContainer.getChildren().clear();

        List<Employee> employees = resolveEmployees();
        if (employees.isEmpty()) {
            ganttContainer.getChildren().add(createEmptyState("No employee workload data available."));
            return;
        }

        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (Employee employee : employees) {
            VBox employeeCard = new VBox(10);
            employeeCard.setStyle("-fx-background-color: rgba(17,24,39,0.6); -fx-background-radius: 12; -fx-padding: 14;");

            HBox employeeHeader = new HBox(10);
            Label employeeLabel = new Label(employee.getName() == null ? "Employee" : employee.getName());
            employeeLabel.setPrefWidth(EMP_WIDTH);
            employeeLabel.setMinWidth(EMP_WIDTH);
            employeeLabel.setMaxWidth(EMP_WIDTH);
            employeeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            Separator separator = new Separator();
            separator.setOrientation(Orientation.VERTICAL);
            separator.setPrefHeight(24);

            Label metaLabel = new Label(buildEmployeeMeta(employee));
            metaLabel.setStyle("-fx-text-fill: #9CA3AF;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            employeeHeader.getChildren().addAll(employeeLabel, separator, metaLabel, spacer);

            List<Tasks> tasks = loadTasksForEmployee(employee);
            if (tasks.isEmpty()) {
                Label empty = new Label("No tasks assigned.");
                empty.setStyle("-fx-text-fill: #9CA3AF; -fx-font-style: italic;");
                employeeCard.getChildren().addAll(employeeHeader, empty);
            } else {
                employeeCard.getChildren().add(employeeHeader);
                for (Tasks task : tasks) {
                    employeeCard.getChildren().add(createTaskRow(task, weekStart));
                }
            }

            ganttContainer.getChildren().add(employeeCard);
        }
    }

    private VBox createEmptyState(String message) {
        VBox emptyState = new VBox();
        emptyState.setStyle("-fx-padding: 20; -fx-background-color: rgba(17,24,39,0.6); -fx-background-radius: 12;");

        Label label = new Label(message);
        label.setStyle("-fx-text-fill: #9CA3AF;");
        emptyState.getChildren().add(label);
        return emptyState;
    }

    private VBox createTaskRow(Tasks task, LocalDate weekStart) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 2 0 2 0;");

        Label taskLabel = new Label(task.getTitle() == null ? "Untitled task" : task.getTitle());
        taskLabel.setPrefWidth(EMP_WIDTH);
        taskLabel.setMinWidth(EMP_WIDTH);
        taskLabel.setMaxWidth(EMP_WIDTH);
        taskLabel.setStyle("-fx-text-fill: white;");

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPrefHeight(26);

        GridPane timeline = buildTimeline(task, weekStart);

        row.getChildren().addAll(taskLabel, separator, timeline);

        VBox wrapper = new VBox(6, row);
        wrapper.setStyle("-fx-padding: 4 0 8 0;");
        return wrapper;
    }

    private GridPane buildTimeline(Tasks task, LocalDate weekStart) {
        GridPane grid = new GridPane();
        for (int day = 0; day < 7; day++) {
            Region cell = new Region();
            cell.setPrefWidth(DAY_WIDTH);
            cell.setPrefHeight(28);
            cell.setStyle("-fx-background-color: rgba(75,85,99,0.45); -fx-background-radius: 6;");
            grid.add(cell, day, 0);
        }

        LocalDate startDate = resolveDate(task.getStart_date(), weekStart);
        LocalDate endDate = resolveDate(task.getEnd_date(), startDate);
        int startIndex = clamp((int) java.time.temporal.ChronoUnit.DAYS.between(weekStart, startDate), 0, 6);
        int endIndex = clamp((int) java.time.temporal.ChronoUnit.DAYS.between(weekStart, endDate), startIndex, 6);

        StackPane bar = new StackPane();
        bar.setPrefHeight(28);
        bar.setMinHeight(28);
        bar.setStyle("-fx-background-color: linear-gradient(to right, #6366F1, #22C55E); -fx-background-radius: 6;");

        Label barLabel = new Label(buildTaskBadge(task));
        barLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        bar.getChildren().add(barLabel);

        GridPane.setColumnIndex(bar, startIndex);
        GridPane.setColumnSpan(bar, Math.max(1, endIndex - startIndex + 1));
        GridPane.setRowIndex(bar, 0);
        grid.getChildren().add(bar);

        return grid;
    }

    private List<Employee> resolveEmployees() {
        List<Employee> employees = new ArrayList<>();
        try {
            User user = SessionManager.getUser();
            if (user == null) {
                List<Employee> allEmployees = employeeService.getAllEmployees();
                return allEmployees == null ? employees : allEmployees;
            }

            if (user.isEmployee()) {
                Employee current = employeeService.getEmployeeByUserId(user.getUserId());
                if (current != null) {
                    employees.add(current);
                }
                return employees;
            }

            if (user.isManager()) {
                List<Employee> team = employeeService.getEmployeesByManager(user.getUserId());
                if (team != null && !team.isEmpty()) {
                    return team;
                }
            }

            List<Employee> allEmployees = employeeService.getAllEmployees();
            return allEmployees == null ? employees : allEmployees;
        } catch (Exception ignored) {
            return employees;
        }
    }

    private List<Tasks> loadTasksForEmployee(Employee employee) {
        try {
            List<Tasks> tasks = taskService.getTaskByEmp(employee.getEmp_id());
            return tasks == null ? new ArrayList<>() : tasks;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private LocalDate resolveDate(Date date, LocalDate fallback) {
        if (date == null) {
            return fallback;
        }
        return date.toLocalDate();
    }

    private String buildEmployeeMeta(Employee employee) {
        String department = employee.getDepartment() == null ? "General" : employee.getDepartment();
        String status = employee.getStatus() == null ? "Unknown" : employee.getStatus();
        return department + " • " + status + " • " + employee.getAllocated_hours() + "/" + employee.getWeeklyCapacity() + "h";
    }

    private String buildTaskBadge(Tasks task) {
        String status = task.getStatus() == null ? "Pending" : task.getStatus();
        String priority = task.getPriority() == null ? "Normal" : task.getPriority();
        return task.getTitle() + " • " + status + " • " + priority;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}