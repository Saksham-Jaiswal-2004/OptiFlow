package com.optiflow.controllers;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @FXML
    private VBox commentsFeed;

    private final DateTimeFormatter deadlineFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    public void initialize() {
        ObservableList<TaskRow> tasks = buildMockTasks();
        configureTasksTable(tasks);
        configureWorklog(tasks);
        configureProgress(tasks);
        configureDeadlines();
        configureComments();
        playCardFadeIn();
    }

    @FXML
    private void handleSubmitWorklog() {
        String task = worklogTaskCombo.getValue() == null ? "Task" : worklogTaskCombo.getValue();
        String hours = hoursField.getText() == null ? "0" : hoursField.getText().trim();
        String notes = notesArea.getText() == null ? "No notes" : notesArea.getText().trim();

        addComment("You", "Logged " + hours + "h for " + task + " - " + notes);

        hoursField.clear();
        notesArea.clear();
    }

    private ObservableList<TaskRow> buildMockTasks() {
        return FXCollections.observableArrayList(
                new TaskRow("Finalize sprint API contract", "In Progress", LocalDate.now().plusDays(1)),
                new TaskRow("Fix dashboard edge-case bug", "Pending", LocalDate.now().plusDays(2)),
                new TaskRow("Refactor employee worklog flow", "Completed", LocalDate.now().minusDays(1)),
                new TaskRow("Prepare UAT checklist", "Pending", LocalDate.now().plusDays(5)),
                new TaskRow("Review manager comments", "In Progress", LocalDate.now().plusDays(3))
        );
    }

    private void configureTasksTable(ObservableList<TaskRow> tasks) {
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

        tasksTable.setItems(tasks);
    }

    private void configureWorklog(ObservableList<TaskRow> tasks) {
        for (TaskRow row : tasks) {
            worklogTaskCombo.getItems().add(row.getTaskName());
        }
        if (!worklogTaskCombo.getItems().isEmpty()) {
            worklogTaskCombo.getSelectionModel().selectFirst();
        }
    }

    private void configureProgress(ObservableList<TaskRow> tasks) {
        long completed = tasks.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count();
        long pending = tasks.size() - completed;

        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed", completed),
                new PieChart.Data("Pending", pending)
        );

        progressChart.setData(chartData);
        int percent = tasks.isEmpty() ? 0 : (int) Math.round((completed * 100.0) / tasks.size());
        progressLabel.setText(percent + "% completed");
    }

    private void configureDeadlines() {
        List<String> deadlines = List.of(
                "[URGENT] Finalize sprint API contract - " + LocalDate.now().plusDays(1).format(deadlineFmt),
                "Fix dashboard edge-case bug - " + LocalDate.now().plusDays(2).format(deadlineFmt),
                "Review manager comments - " + LocalDate.now().plusDays(3).format(deadlineFmt),
                "Prepare UAT checklist - " + LocalDate.now().plusDays(5).format(deadlineFmt)
        );

        deadlinesList.setItems(FXCollections.observableArrayList(deadlines));
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

    private void configureComments() {
        addComment("RM", "Please update worklog for API contract before EOD.");
        addComment("AS", "Shared the latest test notes in the task thread.");
        addComment("PM", "Sprint demo moved to 4:30 PM.");
    }

    private void addComment(String initials, String message) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("emp-comment-avatar-wrap");
        Circle circle = new Circle(13);
        circle.getStyleClass().add("emp-comment-avatar-circle");
        Label initialsLabel = new Label(initials);
        initialsLabel.getStyleClass().add("emp-comment-avatar-text");
        avatar.getChildren().addAll(circle, initialsLabel);

        Label msg = new Label(message);
        msg.getStyleClass().add("emp-comment-message");
        msg.setWrapText(true);

        HBox row = new HBox(10, avatar, msg);
        row.getStyleClass().add("emp-comment-row");
        commentsFeed.getChildren().add(row);
    }

    private void playCardFadeIn() {
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
        private final StringProperty taskName;
        private final StringProperty status;
        private final StringProperty deadline;

        public TaskRow(String taskName, String status, LocalDate deadline) {
            this.taskName = new SimpleStringProperty(taskName);
            this.status = new SimpleStringProperty(status);
            this.deadline = new SimpleStringProperty(deadline.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
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
    }
}
