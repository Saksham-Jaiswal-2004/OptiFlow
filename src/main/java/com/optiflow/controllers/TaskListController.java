package com.optiflow.controllers;

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

    @FXML
    public void initialize() {
        configureColumns();
        seedRows();
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

    private void seedRows() {
        rows.setAll(
                new TaskRow("UI Revamp", "Aarav Mehta", "In Progress", "10 Apr 2026", "Polish dashboard cards"),
                new TaskRow("API Hardening", "Neha Singh", "Pending", "08 Apr 2026", "Add validation checks"),
                new TaskRow("Role Guards", "Rohan Patel", "Completed", "05 Apr 2026", "Restrict manager scope"),
                new TaskRow("Export Module", "Ishita Verma", "In Progress", "12 Apr 2026", "CSV + Excel support")
        );
    }

    private void openTaskDetail(TaskRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TaskDetail.fxml"));
            Parent root = loader.load();

            Stage detailStage = new Stage();
            detailStage.setTitle("Task Detail - " + row.getTask());
            detailStage.setScene(new Scene(root, 900, 650));
            detailStage.initModality(Modality.APPLICATION_MODAL);

            Stage owner = (Stage) taskTable.getScene().getWindow();
            detailStage.initOwner(owner);
            detailStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TaskRow {
        private final StringProperty task;
        private final StringProperty assignedTo;
        private final StringProperty status;
        private final StringProperty deadline;
        private final StringProperty comments;

        public TaskRow(String task, String assignedTo, String status, String deadline, String comments) {
            this.task = new SimpleStringProperty(task);
            this.assignedTo = new SimpleStringProperty(assignedTo);
            this.status = new SimpleStringProperty(status);
            this.deadline = new SimpleStringProperty(deadline);
            this.comments = new SimpleStringProperty(comments);
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
