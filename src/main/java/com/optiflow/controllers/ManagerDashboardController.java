package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    private TableColumn<TaskRow, String> taskIdColumn;

    @FXML
    private TableColumn<TaskRow, String> taskDescriptionColumn;

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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final EmployeeService employeeService = new EmployeeService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        Employee manager = resolveCurrentManager();
        ObservableList<Employee> teamMembers = resolveTeamMembers(manager);
        ObservableList<Tasks> teamTasks = resolveTasksForTeam(teamMembers);

        ObservableList<TeamRow> teamRows = loadTeamRows(teamMembers, teamTasks);
        ObservableList<TaskRow> taskRows = loadTaskRows(teamMembers, teamTasks);

        configureTeamOverviewTable(teamRows);
        configureTaskTrackingTable(taskRows);
        buildWeeklyGantt(teamMembers, teamTasks);
        configureCharts(teamRows, taskRows);

        animateCardsOnLoad();
        animateChartsOnLoad();
    }

    private ObservableList<TeamRow> loadTeamRows(ObservableList<Employee> teamMembers, ObservableList<Tasks> teamTasks) {
        ObservableList<TeamRow> rows = FXCollections.observableArrayList();

        try {
            for (Employee member : teamMembers) {
                long assigned = teamTasks.stream()
                        .filter(t -> t.getAssigned_to() == member.getEmp_id() || t.getAssigned_to() == member.getUser_id())
                        .count();
                long completed = teamTasks.stream()
                        .filter(t -> t.getAssigned_to() == member.getEmp_id() || t.getAssigned_to() == member.getUser_id())
                        .filter(t -> "completed".equalsIgnoreCase(t.getStatus()))
                        .count();

                int capacity = Math.max(1, member.getWeeklyCapacity());
                int workloadPct = Math.min(100, (int) Math.round(member.getAllocated_hours() * 100.0 / capacity));

                rows.add(new TeamRow(
                        member.getName(),
                        member.getDesignation(),
                        (int) assigned,
                        (int) completed,
                        workloadPct
                ));
            }
        } catch (Exception ignored) {
        }

        return rows;
    }

    private ObservableList<TaskRow> loadTaskRows(ObservableList<Employee> teamMembers, ObservableList<Tasks> teamTasks) {
        ObservableList<TaskRow> rows = FXCollections.observableArrayList();

        try {
            Map<Integer, String> assigneeNameById = new LinkedHashMap<>();
            for (Employee member : teamMembers) {
                assigneeNameById.put(member.getEmp_id(), member.getName());
                assigneeNameById.put(member.getUser_id(), member.getName());
            }

            for (Tasks task : teamTasks) {
                String assignedName = assigneeNameById.getOrDefault(task.getAssigned_to(), "Unknown Assignee");

                LocalDate deadline = task.getEnd_date() == null ? null : task.getEnd_date().toLocalDate();
                    rows.add(new TaskRow(
                        "T-" + task.getTask_id(),
                        task.getTitle(),
                        safeText(task.getDescription()),
                        assignedName,
                        normalizeStatus(task.getStatus()),
                        deadline
                    ));
            }
        } catch (Exception ignored) {
        }

        return rows;
    }

    private Employee resolveCurrentManager() {
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

    private ObservableList<Employee> resolveTeamMembers(Employee manager) {
        ObservableList<Employee> team = FXCollections.observableArrayList();
        if (manager == null) {
            return team;
        }

        try {
            Map<Integer, Employee> unique = new LinkedHashMap<>();

            List<Employee> byEmpId = employeeService.getEmployeesByManager(manager.getEmp_id());
            if (byEmpId != null) {
                for (Employee employee : byEmpId) {
                    unique.put(employee.getEmp_id(), employee);
                }
            }

            List<Employee> byUserId = employeeService.getEmployeesByManager(manager.getUser_id());
            if (byUserId != null) {
                for (Employee employee : byUserId) {
                    unique.put(employee.getEmp_id(), employee);
                }
            }

            team.addAll(unique.values());
        } catch (Exception ignored) {
        }

        return team;
    }

    private ObservableList<Tasks> resolveTasksForTeam(ObservableList<Employee> teamMembers) {
        ObservableList<Tasks> tasks = FXCollections.observableArrayList();
        if (teamMembers == null || teamMembers.isEmpty()) {
            return tasks;
        }

        try {
            Set<Integer> candidateAssigneeIds = new HashSet<>();
            for (Employee member : teamMembers) {
                candidateAssigneeIds.add(member.getEmp_id());
                candidateAssigneeIds.add(member.getUser_id());
            }

            Map<Integer, Tasks> unique = new LinkedHashMap<>();

            List<Tasks> allTasks = taskService.getAllTasks();
            if (allTasks != null) {
                for (Tasks task : allTasks) {
                    if (candidateAssigneeIds.contains(task.getAssigned_to())) {
                        unique.put(task.getTask_id(), task);
                    }
                }
            }

            for (Employee member : teamMembers) {
                List<Tasks> employeeTasks = taskService.getTaskByEmp(member.getEmp_id());
                if (employeeTasks == null) {
                    employeeTasks = List.of();
                }
                for (Tasks task : employeeTasks) {
                    unique.put(task.getTask_id(), task);
                }

                List<Tasks> employeeTasksByUserId = taskService.getTaskByEmp(member.getUser_id());
                if (employeeTasksByUserId != null) {
                    for (Tasks task : employeeTasksByUserId) {
                        unique.put(task.getTask_id(), task);
                    }
                }
            }
            tasks.addAll(unique.values());
        } catch (Exception ignored) {
        }

        return tasks;
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "Pending";
        }
        String s = status.toLowerCase(Locale.ROOT);
        if ("completed".equals(s)) {
            return "Completed";
        }
        if ("in progress".equals(s) || "active".equals(s)) {
            return "In Progress";
        }
        return "Pending";
    }

    private void configureTeamOverviewTable(ObservableList<TeamRow> rows) {
        memberNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        memberRoleColumn.setCellValueFactory(data -> data.getValue().roleProperty());
        tasksAssignedColumn.setCellValueFactory(data -> data.getValue().tasksAssignedProperty());
        tasksCompletedColumn.setCellValueFactory(data -> data.getValue().tasksCompletedProperty());
        workloadColumn.setCellValueFactory(data -> data.getValue().workloadProperty());

        memberNameColumn.setStyle("-fx-alignment: CENTER;");
        memberRoleColumn.setStyle("-fx-alignment: CENTER;");
        tasksAssignedColumn.setStyle("-fx-alignment: CENTER;");
        tasksCompletedColumn.setStyle("-fx-alignment: CENTER;");
        workloadColumn.setStyle("-fx-alignment: CENTER;");

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
                row.setAlignment(Pos.CENTER);
                setGraphic(row);
                setText(null);
            }
        });

        teamTable.setItems(rows);
    }

    private void configureTaskTrackingTable(ObservableList<TaskRow> rows) {
        taskIdColumn.setCellValueFactory(data -> data.getValue().taskIdProperty());
        taskNameColumn.setCellValueFactory(data -> data.getValue().taskProperty());
        taskDescriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        taskAssignedToColumn.setCellValueFactory(data -> data.getValue().assignedToProperty());
        taskStatusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        taskDeadlineColumn.setCellValueFactory(data -> data.getValue().deadlineTextProperty());

        taskDescriptionColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }
                String trimmed = item.length() > 48 ? item.substring(0, 48) + "..." : item;
                setText(trimmed);
                setTooltip(new javafx.scene.control.Tooltip(item));
            }
        });

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

    private void buildWeeklyGantt(ObservableList<Employee> teamMembers, ObservableList<Tasks> teamTasks) {
        ganttContainer.getChildren().clear();

        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.getStyleClass().add("mgr-gantt-timeline");
        headerRow.setFillHeight(true);
        headerRow.setMaxWidth(Double.MAX_VALUE);

        Label employeeHeader = new Label("Employee");
        employeeHeader.getStyleClass().add("mgr-gantt-task");
        employeeHeader.setMinWidth(170);
        employeeHeader.setPrefWidth(170);
        employeeHeader.setMaxWidth(170);

        GridPane timeline = createGanttGrid();
        HBox.setHgrow(timeline, Priority.ALWAYS);
        timeline.setMaxWidth(Double.MAX_VALUE);

        List<String> labels = List.of("Mon", "Tue", "Wed", "Thu", "Fri");
        for (int dayIndex = 0; dayIndex < labels.size(); dayIndex++) {
            String day = labels.get(dayIndex);
            Label dayLabel = new Label(day);
            dayLabel.getStyleClass().add("mgr-gantt-day");

            StackPane dayWrap = new StackPane(dayLabel);
            dayWrap.getStyleClass().add("mgr-gantt-day-wrap");
            dayWrap.setMaxWidth(Double.MAX_VALUE);
            timeline.add(dayWrap, dayIndex, 0);
        }

        headerRow.getChildren().addAll(employeeHeader, timeline);
        ganttContainer.getChildren().add(headerRow);

        LocalDate weekStart = resolveBestWeekStart(teamMembers, teamTasks);
        LocalDate weekEnd = weekStart.plusDays(4);

        for (Employee employee : teamMembers) {
            HBox employeeRow = new HBox(10);
            employeeRow.getStyleClass().add("mgr-gantt-row");
            employeeRow.setAlignment(Pos.CENTER_LEFT);
            employeeRow.setMaxWidth(Double.MAX_VALUE);

            Label employeeLabel = new Label(employee.getName());
            employeeLabel.getStyleClass().add("mgr-gantt-task");
            employeeLabel.setMinWidth(170);
            employeeLabel.setPrefWidth(170);
            employeeLabel.setMaxWidth(170);

            GridPane timelineRow = createGanttGrid();
            HBox.setHgrow(timelineRow, Priority.ALWAYS);
            timelineRow.setMaxWidth(Double.MAX_VALUE);

            int[] activeCounts = loadEmployeeActiveTaskCounts(employee, teamTasks, weekStart, weekEnd);
            for (int day = 0; day < 5; day++) {
                int count = activeCounts[day];

                Region bar = new Region();
                bar.getStyleClass().add("mgr-gantt-bar");
                if (count >= 3) {
                    bar.getStyleClass().add("mgr-gantt-bar-a");
                } else if (count == 2) {
                    bar.getStyleClass().add("mgr-gantt-bar-b");
                } else if (count == 1) {
                    bar.getStyleClass().add("mgr-gantt-bar-c");
                } else {
                    bar.setStyle("-fx-opacity: 0.25;");
                }
                bar.setPrefHeight(14);
                bar.setMaxWidth(Double.MAX_VALUE);

                Label countLabel = new Label(String.valueOf(count));
                countLabel.getStyleClass().add("proj-page-text");

                VBox dayCell = new VBox(4, bar, countLabel);
                dayCell.setAlignment(Pos.CENTER);
                dayCell.getStyleClass().add("mgr-gantt-day-wrap");
                dayCell.setMaxWidth(Double.MAX_VALUE);
                timelineRow.add(dayCell, day, 0);
            }

            employeeRow.getChildren().addAll(employeeLabel, timelineRow);
            ganttContainer.getChildren().add(employeeRow);
        }

        if (teamMembers.isEmpty()) {
            Label empty = new Label("No team members found for weekly gantt.");
            empty.getStyleClass().add("proj-page-text");
            ganttContainer.getChildren().add(empty);
        }

        ganttContainer.setFillWidth(true);
    }

    private GridPane createGanttGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        for (int i = 0; i < 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(20);
            col.setHgrow(Priority.ALWAYS);
            col.setFillWidth(true);
            grid.getColumnConstraints().add(col);
        }
        return grid;
    }

    private LocalDate resolveBestWeekStart(ObservableList<Employee> teamMembers, ObservableList<Tasks> teamTasks) {
        LocalDate currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentWeekEnd = currentWeekStart.plusDays(4);

        Map<LocalDate, Set<Integer>> occupancyByWeek = new LinkedHashMap<>();
        occupancyByWeek.put(currentWeekStart, new HashSet<>());

        for (Tasks task : teamTasks) {
            Employee assignee = resolveAssignee(task, teamMembers);
            if (assignee == null) {
                continue;
            }

            LocalDate[] span = resolveTaskSpan(task, currentWeekStart, currentWeekEnd);
            LocalDate start = span[0];
            LocalDate end = span[1];

            LocalDate weekCursor = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastWeek = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            while (!weekCursor.isAfter(lastWeek)) {
                occupancyByWeek.computeIfAbsent(weekCursor, ignored -> new HashSet<>()).add(assignee.getEmp_id());
                weekCursor = weekCursor.plusWeeks(1);
            }
        }

        LocalDate bestWeek = currentWeekStart;
        int bestCount = -1;
        long bestDistance = Long.MAX_VALUE;
        for (Map.Entry<LocalDate, Set<Integer>> entry : occupancyByWeek.entrySet()) {
            int count = entry.getValue().size();
            long distance = Math.abs(ChronoUnit.DAYS.between(currentWeekStart, entry.getKey()));
            if (count > bestCount || (count == bestCount && distance < bestDistance)) {
                bestCount = count;
                bestDistance = distance;
                bestWeek = entry.getKey();
            }
        }

        return bestWeek;
    }

    private Employee resolveAssignee(Tasks task, ObservableList<Employee> teamMembers) {
        for (Employee member : teamMembers) {
            if (task.getAssigned_to() == member.getEmp_id() || task.getAssigned_to() == member.getUser_id()) {
                return member;
            }
        }
        return null;
    }

    private LocalDate[] resolveTaskSpan(Tasks task, LocalDate fallbackStart, LocalDate fallbackEnd) {
        LocalDate taskStart = task.getStart_date() == null ? null : task.getStart_date().toLocalDate();
        LocalDate taskEnd = task.getEnd_date() == null ? null : task.getEnd_date().toLocalDate();

        if (taskStart == null && taskEnd == null) {
            return new LocalDate[]{fallbackStart, fallbackEnd};
        }

        if (taskStart == null) {
            taskStart = taskEnd;
        }
        if (taskEnd == null) {
            taskEnd = taskStart;
        }

        if (taskEnd.isBefore(taskStart)) {
            LocalDate swap = taskStart;
            taskStart = taskEnd;
            taskEnd = swap;
        }

        return new LocalDate[]{taskStart, taskEnd};
    }

    private int[] loadEmployeeActiveTaskCounts(Employee employee, ObservableList<Tasks> teamTasks, LocalDate weekStart, LocalDate weekEnd) {
        int[] activeCounts = new int[]{0, 0, 0, 0, 0};

        for (Tasks task : teamTasks) {
            if (task.getAssigned_to() != employee.getEmp_id() && task.getAssigned_to() != employee.getUser_id()) {
                continue;
            }

            LocalDate[] span = resolveTaskSpan(task, weekStart, weekStart);
            LocalDate taskStart = span[0];
            LocalDate taskEnd = span[1];

            if (taskEnd.isBefore(weekStart) || taskStart.isAfter(weekEnd)) {
                continue;
            }

            for (int day = 0; day < 5; day++) {
                LocalDate current = weekStart.plusDays(day);
                if (!current.isBefore(taskStart) && !current.isAfter(taskEnd)) {
                    activeCounts[day] += 1;
                }
            }
        }

        return activeCounts;
    }

    private void configureCharts(ObservableList<TeamRow> teamRows, ObservableList<TaskRow> taskRows) {
        BarChart.Series<String, Number> series = new BarChart.Series<>();
        series.setName("Tasks Completed");
        for (TeamRow row : teamRows) {
            series.getData().add(new BarChart.Data<>(row.getName(), row.getTasksCompleted()));
        }
        performanceBarChart.getData().setAll(series);

        long completed = taskRows.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count();
        long pending = Math.max(0, taskRows.size() - completed);

        completionPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Completed", completed),
                new PieChart.Data("Pending", pending)
        ));
    }

    private void animateCardsOnLoad() {
        if (root == null) {
            return;
        }

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
        private final StringProperty taskId;
        private final StringProperty task;
        private final StringProperty description;
        private final StringProperty assignedTo;
        private final StringProperty status;
        private final ObjectProperty<LocalDate> deadline;
        private final StringProperty deadlineText;

        public TaskRow(String taskId, String task, String description, String assignedTo, String status, LocalDate deadline) {
            this.taskId = new SimpleStringProperty(taskId);
            this.task = new SimpleStringProperty(task);
            this.description = new SimpleStringProperty(description);
            this.assignedTo = new SimpleStringProperty(assignedTo);
            this.status = new SimpleStringProperty(status);
            this.deadline = new SimpleObjectProperty<>(deadline);
            this.deadlineText = new SimpleStringProperty(deadline == null ? "-" : deadline.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        }

        public StringProperty taskIdProperty() {
            return taskId;
        }

        public String getTask() {
            return task.get();
        }

        public String getAssignedTo() {
            return assignedTo.get();
        }

        public StringProperty descriptionProperty() {
            return description;
        }

        public String getStatus() {
            return status.get();
        }

        public boolean isOverdue() {
            return deadline.get() != null && deadline.get().isBefore(LocalDate.now()) && !"Completed".equalsIgnoreCase(getStatus());
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

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
