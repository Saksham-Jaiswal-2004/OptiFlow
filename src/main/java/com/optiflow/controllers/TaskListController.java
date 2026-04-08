package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Duration;

public class TaskListController {

    @FXML
    private TableView<TaskRow> taskTable;

    @FXML
    private TableColumn<TaskRow, String> taskColumn;

    @FXML
    private TableColumn<TaskRow, String> assignedToColumn;

    @FXML
    private TableColumn<TaskRow, String> statusColumn;

    @FXML
    private TableColumn<TaskRow, String> deadlineColumn;

    @FXML
    private TableColumn<TaskRow, String> commentsColumn;

    private final ObservableList<TaskRow> rows = FXCollections.observableArrayList();
    private final TaskService taskService = new TaskService();
    private final EmployeeService employeeService = new EmployeeService();
    private Timeline autoRefreshTimeline;

    @FXML
    public void initialize() {
        configureColumns();
        loadRowsFromServices();
        taskTable.setItems(rows);
        startAutoRefresh();

        taskTable.setRowFactory(tv -> {
            TableRow<TaskRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    openTaskDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void configureColumns() {
        taskColumn.setCellValueFactory(data -> data.getValue().taskProperty());
        assignedToColumn.setCellValueFactory(data -> data.getValue().assignedToProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        deadlineColumn.setCellValueFactory(data -> data.getValue().deadlineProperty());
        commentsColumn.setCellValueFactory(data -> data.getValue().commentsProperty());
    }

    private void loadRowsFromServices() {
        rows.clear();

        try {
            Map<Integer, String> assigneeNameById = resolveAssigneeNameMapForCurrentUser();
            List<Tasks> tasks = resolveTasksForCurrentUser();
            if (tasks == null) {
                return;
            }

            for (Tasks task : tasks) {
                String assignedTo = assigneeNameById.getOrDefault(task.getAssigned_to(), resolveEmployeeName(task.getAssigned_to()));
                String deadline = task.getEnd_date() == null ? "-" : task.getEnd_date().toLocalDate().toString();
                String comments = task.getDescription() == null ? "" : task.getDescription();

                rows.add(new TaskRow(
                        task,
                        task.getTitle() == null ? "Untitled Task" : task.getTitle(),
                        assignedTo,
                        normalizeStatus(task.getStatus()),
                        deadline,
                        comments
                ));
            }
        } catch (Exception ignored) {
        }
    }

    private Map<Integer, String> resolveAssigneeNameMapForCurrentUser() {
        Map<Integer, String> assigneeNameById = new LinkedHashMap<>();

        try {
            User user = SessionManager.getUser();
            if (user != null && user.isEmployee()) {
                Employee self = employeeService.getEmployeeByUserId(user.getUserId());
                if (self != null && self.getName() != null && !self.getName().isBlank()) {
                    assigneeNameById.put(self.getEmp_id(), self.getName());
                    assigneeNameById.put(self.getUser_id(), self.getName());
                }
            }

            if (user != null && user.isManager()) {
                Employee manager = employeeService.getEmployeeByUserId(user.getUserId());
                if (manager != null) {
                    List<Employee> teamMembers = resolveTeamMembersForManager(manager);
                    for (Employee member : teamMembers) {
                        if (member.getName() != null && !member.getName().isBlank()) {
                            assigneeNameById.put(member.getEmp_id(), member.getName());
                            assigneeNameById.put(member.getUser_id(), member.getName());
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return assigneeNameById;
    }

    private List<Tasks> resolveTasksForCurrentUser() throws Exception {
        User user = SessionManager.getUser();
        if (user == null || user.isAdmin()) {
            return taskService.getAllTasks();
        }

        if (user.isEmployee()) {
            Employee self = employeeService.getEmployeeByUserId(user.getUserId());
            if (self == null) {
                return FXCollections.observableArrayList();
            }

            return resolveTasksForEmployee(self);
        }

        if (user.isManager()) {
            Employee manager = employeeService.getEmployeeByUserId(user.getUserId());
            if (manager == null) {
                return List.of();
            }

            List<Employee> teamMembers = resolveTeamMembersForManager(manager);
            if (teamMembers.isEmpty()) {
                return List.of();
            }

            return resolveTasksForTeam(teamMembers);
        }

        return taskService.getAllTasks();
    }

    private String resolveEmployeeName(int empId) {
        try {
            Employee employee = employeeService.getEmployeeById(empId);
            if (employee == null) {
                employee = employeeService.getEmployeeByUserId(empId);
            }
            return employee == null || employee.getName() == null ? "Unassigned" : employee.getName();
        } catch (Exception ignored) {
            return "Unassigned";
        }
    }

    private List<Employee> resolveTeamMembersForManager(Employee manager) {
        Map<Integer, Employee> unique = new LinkedHashMap<>();

        try {
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
        } catch (Exception ignored) {
        }

        return List.copyOf(unique.values());
    }

    private List<Tasks> resolveTasksForTeam(List<Employee> teamMembers) {
        Map<Integer, Tasks> unique = new LinkedHashMap<>();

        try {
            Set<Integer> candidateAssigneeIds = new HashSet<>();
            for (Employee member : teamMembers) {
                candidateAssigneeIds.add(member.getEmp_id());
                candidateAssigneeIds.add(member.getUser_id());
            }

            List<Tasks> allTasks = taskService.getAllTasks();
            if (allTasks != null) {
                for (Tasks task : allTasks) {
                    if (candidateAssigneeIds.contains(task.getAssigned_to())) {
                        unique.put(task.getTask_id(), task);
                    }
                }
            }

            for (Employee member : teamMembers) {
                List<Tasks> byEmpId = taskService.getTaskByEmp(member.getEmp_id());
                if (byEmpId != null) {
                    for (Tasks task : byEmpId) {
                        unique.put(task.getTask_id(), task);
                    }
                }

                List<Tasks> byUserId = taskService.getTaskByEmp(member.getUser_id());
                if (byUserId != null) {
                    for (Tasks task : byUserId) {
                        unique.put(task.getTask_id(), task);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return List.copyOf(unique.values());
    }

    private List<Tasks> resolveTasksForEmployee(Employee employee) {
        Map<Integer, Tasks> unique = new LinkedHashMap<>();

        try {
            Set<Integer> candidateAssigneeIds = new HashSet<>();
            candidateAssigneeIds.add(employee.getEmp_id());
            candidateAssigneeIds.add(employee.getUser_id());

            List<Tasks> allTasks = taskService.getAllTasks();
            if (allTasks != null) {
                for (Tasks task : allTasks) {
                    if (candidateAssigneeIds.contains(task.getAssigned_to())) {
                        unique.put(task.getTask_id(), task);
                    }
                }
            }

            List<Tasks> byEmpId = taskService.getTaskByEmp(employee.getEmp_id());
            if (byEmpId != null) {
                for (Tasks task : byEmpId) {
                    unique.put(task.getTask_id(), task);
                }
            }

            List<Tasks> byUserId = taskService.getTaskByEmp(employee.getUser_id());
            if (byUserId != null) {
                for (Tasks task : byUserId) {
                    unique.put(task.getTask_id(), task);
                }
            }
        } catch (Exception ignored) {
        }

        return List.copyOf(unique.values());
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Pending";
        }

        String normalized = status.trim().toLowerCase();
        if ("completed".equals(normalized)) {
            return "Completed";
        }
        if ("in progress".equals(normalized) || "active".equals(normalized)) {
            return "In Progress";
        }
        return "Pending";
    }

    private void openTaskDetail(TaskRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaskDetail.fxml"));
            Parent root = loader.load();
            TaskDetailPanelController controller = loader.getController();
            controller.setTask(row.getTaskData());

            Stage detailStage = new Stage();
            detailStage.setTitle("Task Detail - " + row.getTask());
            detailStage.setScene(new Scene(root, 1120, 780));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.setMinWidth(1040);
            detailStage.setMinHeight(720);
            detailStage.setResizable(true);

            Stage owner = (Stage) taskTable.getScene().getWindow();
            detailStage.initOwner(owner);
            detailStage.showAndWait();
            refreshRows();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }

        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> refreshRows()));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();

        taskTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && newScene.getWindow() != null) {
                newScene.getWindow().setOnHidden(event -> autoRefreshTimeline.stop());
            }
        });
    }

    private void refreshRows() {
        loadRowsFromServices();
    }

    public static class TaskRow {
        private final Tasks taskData;
        private final StringProperty task;
        private final StringProperty assignedTo;
        private final StringProperty status;
        private final StringProperty deadline;
        private final StringProperty comments;

        public TaskRow(Tasks taskData, String task, String assignedTo, String status, String deadline, String comments) {
            this.taskData = taskData;
            this.task = new SimpleStringProperty(task);
            this.assignedTo = new SimpleStringProperty(assignedTo);
            this.status = new SimpleStringProperty(status);
            this.deadline = new SimpleStringProperty(deadline);
            this.comments = new SimpleStringProperty(comments);
        }

        public Tasks getTaskData() {
            return taskData;
        }

        public String getTask() {
            return task.get();
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

        public StringProperty deadlineProperty() {
            return deadline;
        }

        public StringProperty commentsProperty() {
            return comments;
        }
    }
}
