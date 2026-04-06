package com.optiflow.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManagerDashboardController {

    @FXML
    private VBox root;

    @FXML
    private TableView<TeamRow> teamTable;

    @FXML
    private TableColumn<TeamRow, String> memberNameColumn;

    @FXML
    private TableColumn<TeamRow, String> memberRoleColumn;

    @FXML
    private TableColumn<TeamRow, Number> tasksAssignedColumn;

    @FXML
    private TableColumn<TeamRow, Number> tasksCompletedColumn;

    @FXML
    private TableColumn<TeamRow, Number> workloadColumn;

    @FXML
    private TableView<TaskRow> taskTable;

    @FXML
    private TableColumn<TaskRow, String> taskNameColumn;

    @FXML
    private TableColumn<TaskRow, String> taskAssignedToColumn;

    @FXML
    private TableColumn<TaskRow, String> taskStatusColumn;

    @FXML
    private TableColumn<TaskRow, String> taskDeadlineColumn;

    @FXML
    private VBox ganttContainer;

    @FXML
    private BarChart<String, Number> performanceBarChart;

    @FXML
    private PieChart completionPieChart;

    @FXML
    private VBox alertsList;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    public void initialize() {
        ObservableList<TeamRow> teamRows = mockTeamRows();
        ObservableList<TaskRow> taskRows = mockTaskRows();

        configureTeamOverviewTable(teamRows);
        configureTaskTrackingTable(taskRows);
        buildWeeklyGantt(taskRows);
        configureCharts(teamRows, taskRows);
        buildAlerts(taskRows, teamRows);

        animateCardsOnLoad();
        animateChartsOnLoad();
    }

    private ObservableList<TeamRow> mockTeamRows() {
        return FXCollections.observableArrayList(
                new TeamRow("Aditi Sharma", "Backend Engineer", 8, 6, 78),
                new TeamRow("Rahul Sinha", "QA Engineer", 10, 7, 86),
                new TeamRow("Neha Kapoor", "UI/UX", 5, 4, 62),
                new TeamRow("Imran Khan", "Data Engineer", 9, 5, 92),
                new TeamRow("Priya Menon", "Frontend Engineer", 7, 5, 73)
        );
    }

    private ObservableList<TaskRow> mockTaskRows() {
        return FXCollections.observableArrayList(
                new TaskRow("Sprint API hardening", "Aditi Sharma", "In Progress", LocalDate.now().plusDays(1)),
                new TaskRow("Regression round 2", "Rahul Sinha", "Pending", LocalDate.now().minusDays(1)),
                new TaskRow("Refine task board UX", "Neha Kapoor", "Completed", LocalDate.now().minusDays(2)),
                new TaskRow("Sales forecast ETL", "Imran Khan", "In Progress", LocalDate.now().plusDays(2)),
                new TaskRow("Notification center", "Priya Menon", "Pending", LocalDate.now().plusDays(4))
        );
    }

    private void configureTeamOverviewTable(ObservableList<TeamRow> rows) {
        memberNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        memberRoleColumn.setCellValueFactory(data -> data.getValue().roleProperty());
        tasksAssignedColumn.setCellValueFactory(data -> data.getValue().tasksAssignedProperty());
        tasksCompletedColumn.setCellValueFactory(data -> data.getValue().tasksCompletedProperty());
        workloadColumn.setCellValueFactory(data -> data.getValue().workloadProperty());

        workloadColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                double value = item.doubleValue();
                Rectangle track = new Rectangle(110, 8);
                track.getStyleClass().add("mgr-workload-track");

                Rectangle fill = new Rectangle(Math.min(110, value * 1.1), 8);
                fill.getStyleClass().add("mgr-workload-fill");
                if (value >= 90) {
                    fill.getStyleClass().add("mgr-workload-critical");
                } else if (value >= 80) {
                    fill.getStyleClass().add("mgr-workload-warn");
                }

                Label pct = new Label((int) value + "%");
                pct.getStyleClass().add("mgr-workload-label");

                StackPane bar = new StackPane(track, fill);
                HBox row = new HBox(8, bar, pct);
                setGraphic(row);
                setText(null);
            }
        });

        teamTable.setItems(rows);
    }

    private void configureTaskTrackingTable(ObservableList<TaskRow> rows) {
        taskNameColumn.setCellValueFactory(data -> data.getValue().taskProperty());
        taskAssignedToColumn.setCellValueFactory(data -> data.getValue().assignedToProperty());
        taskStatusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        taskDeadlineColumn.setCellValueFactory(data -> data.getValue().deadlineTextProperty());

        taskStatusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(item);
                badge.getStyleClass().add("mgr-status-badge");
                if ("Completed".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("mgr-status-done");
                } else if ("In Progress".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("mgr-status-progress");
                } else {
                    badge.getStyleClass().add("mgr-status-pending");
                }
                setGraphic(badge);
                setText(null);
            }
        });

        taskDeadlineColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("mgr-overdue-cell");
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                TaskRow row = getTableView().getItems().get(getIndex());
                setText(item);
                if (row.isOverdue()) {
                    getStyleClass().add("mgr-overdue-cell");
                }
            }
        });

        taskTable.setRowFactory(tv -> {
            TableRow<TaskRow> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldRow, newRow) -> {
                row.getStyleClass().remove("mgr-task-overdue-row");
                if (newRow != null && newRow.isOverdue()) {
                    row.getStyleClass().add("mgr-task-overdue-row");
                }
            });
            return row;
        });

        taskTable.setItems(rows);
    }

    private void buildWeeklyGantt(ObservableList<TaskRow> taskRows) {
        ganttContainer.getChildren().clear();

        HBox timeline = new HBox(8);
        timeline.getStyleClass().add("mgr-gantt-timeline");
        List<String> labels = List.of("Mon", "Tue", "Wed", "Thu", "Fri");
        for (String day : labels) {
            Label dayLabel = new Label(day);
            dayLabel.getStyleClass().add("mgr-gantt-day");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox dayWrap = new HBox(dayLabel, spacer);
            HBox.setHgrow(dayWrap, Priority.ALWAYS);
            timeline.getChildren().add(dayWrap);
        }
        ganttContainer.getChildren().add(timeline);

        int barSeed = 0;
        for (TaskRow task : taskRows) {
            HBox row = new HBox(10);
            row.getStyleClass().add("mgr-gantt-row");

            Label taskLabel = new Label(task.getTask());
            taskLabel.getStyleClass().add("mgr-gantt-task");
            taskLabel.setMinWidth(170);

            Region bar = new Region();
            bar.getStyleClass().add("mgr-gantt-bar");
            if (barSeed % 3 == 0) {
                bar.getStyleClass().add("mgr-gantt-bar-a");
            } else if (barSeed % 3 == 1) {
                bar.getStyleClass().add("mgr-gantt-bar-b");
            } else {
                bar.getStyleClass().add("mgr-gantt-bar-c");
            }
            bar.setPrefHeight(16);
            bar.setPrefWidth(120 + (barSeed * 20));

            row.getChildren().addAll(taskLabel, bar);
            ganttContainer.getChildren().add(row);
            barSeed++;
        }
    }

    private void configureCharts(ObservableList<TeamRow> teamRows, ObservableList<TaskRow> taskRows) {
        BarChart.Series<String, Number> series = new BarChart.Series<>();
        series.setName("Tasks Completed");
        for (TeamRow row : teamRows) {
            series.getData().add(new BarChart.Data<>(row.getName(), row.getTasksCompleted()));
        }
        performanceBarChart.getData().setAll(series);

        long completed = taskRows.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count();
        long pending = taskRows.size() - completed;

        completionPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Completed", completed),
                new PieChart.Data("Pending", pending)
        ));
    }

    private void buildAlerts(ObservableList<TaskRow> taskRows, ObservableList<TeamRow> teamRows) {
        alertsList.getChildren().clear();

        taskRows.stream().filter(TaskRow::isOverdue).forEach(task -> {
            Label alert = new Label("Overdue: " + task.getTask() + " (" + task.getAssignedTo() + ")");
            alert.getStyleClass().addAll("mgr-alert-item", "mgr-alert-warn");
            alertsList.getChildren().add(alert);
        });

        teamRows.stream().filter(row -> row.getWorkload() >= 85).forEach(row -> {
            Label alert = new Label("High load: " + row.getName() + " at " + row.getWorkload() + "% workload");
            alert.getStyleClass().addAll("mgr-alert-item", "mgr-alert-soft");
            alertsList.getChildren().add(alert);
        });

        if (alertsList.getChildren().isEmpty()) {
            Label clean = new Label("No urgent alerts. Team workload is balanced.");
            clean.getStyleClass().add("mgr-alert-item");
            alertsList.getChildren().add(clean);
        }
    }

    private void animateCardsOnLoad() {
        int i = 0;
        for (Node node : root.lookupAll(".mgr-card")) {
            node.setOpacity(0);
            node.setScaleX(0.98);
            node.setScaleY(0.98);

            FadeTransition fade = new FadeTransition(Duration.millis(360), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(i * 80L));

            ScaleTransition scale = new ScaleTransition(Duration.millis(360), node);
            scale.setFromX(0.98);
            scale.setFromY(0.98);
            scale.setToX(1);
            scale.setToY(1);
            scale.setDelay(Duration.millis(i * 80L));

            new ParallelTransition(fade, scale).play();
            i++;
        }
    }

    private void animateChartsOnLoad() {
        performanceBarChart.setAnimated(true);
        completionPieChart.setAnimated(true);
    }

    public static class TeamRow {
        private final StringProperty name;
        private final StringProperty role;
        private final IntegerProperty tasksAssigned;
        private final IntegerProperty tasksCompleted;
        private final DoubleProperty workload;

        public TeamRow(String name, String role, int tasksAssigned, int tasksCompleted, double workload) {
            this.name = new SimpleStringProperty(name);
            this.role = new SimpleStringProperty(role);
            this.tasksAssigned = new SimpleIntegerProperty(tasksAssigned);
            this.tasksCompleted = new SimpleIntegerProperty(tasksCompleted);
            this.workload = new SimpleDoubleProperty(workload);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty roleProperty() {
            return role;
        }

        public int getTasksCompleted() {
            return tasksCompleted.get();
        }

        public IntegerProperty tasksAssignedProperty() {
            return tasksAssigned;
        }

        public IntegerProperty tasksCompletedProperty() {
            return tasksCompleted;
        }

        public double getWorkload() {
            return workload.get();
        }

        public DoubleProperty workloadProperty() {
            return workload;
        }
    }

    public static class TaskRow {
        private final StringProperty task;
        private final StringProperty assignedTo;
        private final StringProperty status;
        private final ObjectProperty<LocalDate> deadline;
        private final StringProperty deadlineText;

        public TaskRow(String task, String assignedTo, String status, LocalDate deadline) {
            this.task = new SimpleStringProperty(task);
            this.assignedTo = new SimpleStringProperty(assignedTo);
            this.status = new SimpleStringProperty(status);
            this.deadline = new SimpleObjectProperty<>(deadline);
            this.deadlineText = new SimpleStringProperty(deadline.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        }

        public String getTask() {
            return task.get();
        }

        public String getAssignedTo() {
            return assignedTo.get();
        }

        public String getStatus() {
            return status.get();
        }

        public boolean isOverdue() {
            return deadline.get().isBefore(LocalDate.now()) && !"Completed".equalsIgnoreCase(getStatus());
        }

        public StringProperty taskProperty() {
            return task;
        }

        public StringProperty assignedToProperty() {
            return assignedTo;
        }

        public StringProperty statusProperty() {
            return status;
        }

        public StringProperty deadlineTextProperty() {
            return deadlineText;
        }
    }
}
