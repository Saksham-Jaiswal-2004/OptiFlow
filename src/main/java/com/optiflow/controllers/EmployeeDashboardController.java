package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.services.WorkLogService;
import com.optiflow.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmployeeDashboardController {

    @FXML
    private VBox root;

    @FXML
    private TableView<TaskRow> tasksTable;

    @FXML
    private TableColumn<TaskRow, String> taskNameColumn;

    @FXML
    private TableColumn<TaskRow, String> statusColumn;

    @FXML
    private TableColumn<TaskRow, String> deadlineColumn;

    @FXML
    private ComboBox<String> worklogTaskCombo;

    @FXML
    private TextField hoursField;

    @FXML
    private TextArea notesArea;

    @FXML
    private PieChart progressChart;

    @FXML
    private Label progressLabel;

    @FXML
    private ListView<String> deadlinesList;

    private final DateTimeFormatter deadlineFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TaskService taskService = new TaskService();
    private final EmployeeService employeeService = new EmployeeService();
    private final WorkLogService workLogService = new WorkLogService();

    private final Map<String, Integer> taskIdByLabel = new HashMap<>();
    private final ObservableList<TaskRow> taskRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTasksTable();
        loadTasksFromServices();
        configureWorklog();
        configureProgress();
        configureDeadlines();
        playCardFadeIn();
    }

    @FXML
    private void handleSubmitWorklog() {
        String selectedTask = worklogTaskCombo.getValue();
        String hoursText = hoursField.getText() == null ? "" : hoursField.getText().trim();
        String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();

        if (selectedTask == null || selectedTask.isBlank()) {
            addComment("SYS", "Please select a task before submitting worklog.");
            return;
        }

        int taskId = taskIdByLabel.getOrDefault(selectedTask, -1);
        if (taskId <= 0) {
            addComment("SYS", "Unable to resolve selected task.");
            return;
        }

        int hours;
        try {
            hours = (int) Math.round(Double.parseDouble(hoursText));
            if (hours <= 0) {
                addComment("SYS", "Hours must be greater than 0.");
                return;
            }
        } catch (Exception ex) {
            addComment("SYS", "Enter valid hours (example: 2 or 2.5).");
            return;
        }

        try {
            User user = SessionManager.getUser();
            if (user == null) {
                addComment("SYS", "No active user session.");
                return;
            }

            boolean saved = workLogService.logWork(user.getUserId(), taskId, Date.valueOf(LocalDate.now()), hours, notes);
            if (saved) {
                addComment("YOU", "Worklog submitted for " + selectedTask + " (" + hours + "h)");
                hoursField.clear();
                notesArea.clear();
            } else {
                addComment("SYS", "Could not submit worklog.");
            }
        } catch (Exception ex) {
            addComment("SYS", "Error while submitting worklog.");
        }
    }

    private void loadTasksFromServices() {
        taskRows.clear();

        try {
            User user = SessionManager.getUser();
            if (user == null) {
                tasksTable.setItems(taskRows);
                return;
            }

            Employee employee = employeeService.getEmployeeByUserId(user.getUserId());
            if (employee == null) {
                tasksTable.setItems(taskRows);
                return;
            }

            List<Tasks> tasks = taskService.getTaskByEmp(employee.getEmp_id());
            if (tasks != null) {
                for (Tasks task : tasks) {
                    LocalDate deadline = task.getEnd_date() == null ? LocalDate.now().plusDays(7) : task.getEnd_date().toLocalDate();
                    taskRows.add(new TaskRow(task.getTask_id(), task.getTitle(), normalizeStatus(task.getStatus()), deadline));
                }
            }
        } catch (Exception ignored) {
        }

        tasksTable.setItems(taskRows);
    }

    private void configureTasksTable() {
        taskNameColumn.setCellValueFactory(data -> data.getValue().taskNameProperty());
        deadlineColumn.setCellValueFactory(data -> data.getValue().deadlineProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(item);
                badge.getStyleClass().add("emp-status-badge");
                if ("Completed".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("emp-status-done");
                } else if ("In Progress".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("emp-status-progress");
                } else {
                    badge.getStyleClass().add("emp-status-pending");
                }
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void configureWorklog() {
        taskIdByLabel.clear();
        worklogTaskCombo.getItems().clear();

        for (TaskRow row : taskRows) {
            String label = "T-" + row.getTaskId() + " | " + row.getTaskName();
            worklogTaskCombo.getItems().add(label);
            taskIdByLabel.put(label, row.getTaskId());
        }

        if (!worklogTaskCombo.getItems().isEmpty()) {
            worklogTaskCombo.getSelectionModel().selectFirst();
        }
    }

    private void configureProgress() {
        long completed = taskRows.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count();
        long pending = Math.max(0, taskRows.size() - completed);

        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed", completed),
                new PieChart.Data("Pending", pending)
        );

        progressChart.setData(chartData);
        int percent = taskRows.isEmpty() ? 0 : (int) Math.round((completed * 100.0) / taskRows.size());
        progressLabel.setText(percent + "% completed");
    }

    private void configureDeadlines() {
        ObservableList<String> deadlines = FXCollections.observableArrayList();

        for (TaskRow row : taskRows) {
            LocalDate due = row.getDeadlineDate();
            String prefix = due.isBefore(LocalDate.now()) ? "[URGENT] " : "";
            deadlines.add(prefix + row.getTaskName() + " - " + due.format(deadlineFmt));
        }

        deadlinesList.setItems(deadlines);
        deadlinesList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().remove("emp-deadline-urgent");
                    return;
                }
                setText(item.replace("[URGENT] ", ""));
                getStyleClass().remove("emp-deadline-urgent");
                if (item.startsWith("[URGENT]")) {
                    getStyleClass().add("emp-deadline-urgent");
                }
            }
        });
    }

    private void addComment(String initials, String message) {
        if (initials == null || message == null) {
            return;
        }
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "Pending";
        }
        String s = status.trim().toLowerCase(Locale.ROOT);
        if ("completed".equals(s)) {
            return "Completed";
        }
        if ("in progress".equals(s) || "active".equals(s)) {
            return "In Progress";
        }
        return "Pending";
    }

    private void playCardFadeIn() {
        if (root == null) {
            return;
        }

        int index = 0;
        for (Node card : root.lookupAll(".emp-card")) {
            card.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(360), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(index * 90L));
            ft.play();
            index++;
        }
    }

    public static class TaskRow {
        private final int taskId;
        private final StringProperty taskName;
        private final StringProperty status;
        private final StringProperty deadline;
        private final LocalDate deadlineDate;

        public TaskRow(int taskId, String taskName, String status, LocalDate deadline) {
            this.taskId = taskId;
            this.taskName = new SimpleStringProperty(taskName);
            this.status = new SimpleStringProperty(status);
            this.deadline = new SimpleStringProperty(deadline.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            this.deadlineDate = deadline;
        }

        public int getTaskId() {
            return taskId;
        }

        public String getTaskName() {
            return taskName.get();
        }

        public StringProperty taskNameProperty() {
            return taskName;
        }

        public String getStatus() {
            return status.get();
        }

        public StringProperty statusProperty() {
            return status;
        }

        public StringProperty deadlineProperty() {
            return deadline;
        }

        public LocalDate getDeadlineDate() {
            return deadlineDate;
        }
    }
}
