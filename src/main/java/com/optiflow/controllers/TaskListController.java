package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.TaskService;
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

import java.util.List;

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

    @FXML
    public void initialize() {
        configureColumns();
        loadRowsFromServices();
        taskTable.setItems(rows);

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
            List<Tasks> tasks = taskService.getAllTasks();
            if (tasks == null) {
                return;
            }

            for (Tasks task : tasks) {
                String assignedTo = resolveEmployeeName(task.getAssigned_to());
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

    private String resolveEmployeeName(int empId) {
        try {
            Employee employee = employeeService.getEmployeeById(empId);
            return employee == null || employee.getName() == null ? "Unassigned" : employee.getName();
        } catch (Exception ignored) {
            return "Unassigned";
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
