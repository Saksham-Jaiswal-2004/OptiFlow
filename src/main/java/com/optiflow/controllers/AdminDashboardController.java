package com.optiflow.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController {

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
    private TableColumn<ProjectRow, String> projectNameColumn;

    @FXML
    private TableColumn<ProjectRow, String> managerColumn;

    @FXML
    private TableColumn<ProjectRow, Number> progressColumn;

    @FXML
    private TableColumn<ProjectRow, String> statusColumn;

    @FXML
    private PieChart utilizationChart;

    @FXML
    private LineChart<String, Number> completionLineChart;

    @FXML
    private BarChart<String, Number> performanceBarChart;

    @FXML
    private VBox activityFeed;

    @FXML
    public void initialize() {
        ObservableList<ProjectRow> projects = mockProjects();

        configureKpiCounters();
        configureProjectOverview(projects);
        configureUtilizationChart();
        configureAnalytics();
        configureActivityFeed();

        animateCardsOnLoad();
        completionLineChart.setAnimated(true);
        performanceBarChart.setAnimated(true);
        utilizationChart.setAnimated(true);
    }

    private void configureKpiCounters() {
        animateCounter(projectsValue, 42);
        animateCounter(employeesValue, 186);
        animateCounter(managersValue, 18);
        animateCounter(tasksValue, 724);
    }

    private ObservableList<ProjectRow> mockProjects() {
        return FXCollections.observableArrayList(
                new ProjectRow("OptiFlow Core", "Ritika Mehra", 86, "On Track"),
                new ProjectRow("Retail BI", "Arjun Reddy", 61, "At Risk"),
                new ProjectRow("HRMS Upgrade", "Nikhil Kumar", 73, "On Track"),
                new ProjectRow("Client Portal", "Priya Menon", 48, "Delayed")
        );
    }

    private void configureProjectOverview(ObservableList<ProjectRow> rows) {
        projectNameColumn.setCellValueFactory(data -> data.getValue().projectProperty());
        managerColumn.setCellValueFactory(data -> data.getValue().managerProperty());
        progressColumn.setCellValueFactory(data -> data.getValue().progressProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

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

    private void configureUtilizationChart() {
        utilizationChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Billable", 58),
                new PieChart.Data("Internal", 27),
                new PieChart.Data("Bench", 15)
        ));
    }

    private void configureAnalytics() {
        LineChart.Series<String, Number> completionSeries = new LineChart.Series<>();
        completionSeries.setName("Task Completion");
        completionSeries.getData().addAll(
                List.of(
                        new LineChart.Data<>("Mon", 62),
                        new LineChart.Data<>("Tue", 71),
                        new LineChart.Data<>("Wed", 69),
                        new LineChart.Data<>("Thu", 78),
                        new LineChart.Data<>("Fri", 85)
                )
        );
        completionLineChart.getData().setAll(completionSeries);

        BarChart.Series<String, Number> perfSeries = new BarChart.Series<>();
        perfSeries.setName("Employee Performance");
        perfSeries.getData().addAll(
                List.of(
                        new BarChart.Data<>("Aditi", 17),
                        new BarChart.Data<>("Rahul", 14),
                        new BarChart.Data<>("Neha", 11),
                        new BarChart.Data<>("Imran", 16),
                        new BarChart.Data<>("Priya", 13)
                )
        );
        performanceBarChart.getData().setAll(perfSeries);
    }

    private void configureActivityFeed() {
        activityFeed.getChildren().clear();

        addActivity("AM", "Task assigned to Rahul for API hardening", LocalTime.now().minusMinutes(18));
        addActivity("RM", "Worklog submitted by Neha", LocalTime.now().minusMinutes(47));
        addActivity("NK", "Performance review generated for Sprint 14", LocalTime.now().minusHours(2));
        addActivity("PM", "Project OptiFlow Core moved to 86%", LocalTime.now().minusHours(4));
    }

    private void addActivity(String initials, String message, LocalTime time) {
        StackPane avatar = new StackPane();
        Circle circle = new Circle(13);
        circle.getStyleClass().add("adm-feed-avatar-circle");
        Label initialsLabel = new Label(initials);
        initialsLabel.getStyleClass().add("adm-feed-avatar-text");
        avatar.getChildren().addAll(circle, initialsLabel);

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("adm-feed-message");
        messageLabel.setWrapText(true);

        Label timeLabel = new Label(time.format(DateTimeFormatter.ofPattern("hh:mm a")));
        timeLabel.getStyleClass().add("adm-feed-time");

        VBox textWrap = new VBox(2, messageLabel, timeLabel);
        HBox row = new HBox(10, avatar, textWrap);
        row.getStyleClass().add("adm-feed-row");
        activityFeed.getChildren().add(row);
    }

    private void animateCounter(Label label, int target) {
        IntegerProperty value = new SimpleIntegerProperty(0);
        value.addListener((obs, oldVal, newVal) -> label.setText(String.valueOf(newVal.intValue())));

        Timeline timeline = new Timeline();
        int step = Math.max(1, target / 28);
        for (int current = 0; current <= target; current += step) {
            int snapshot = Math.min(current, target);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis((snapshot / (double) Math.max(1, target)) * 900), e -> value.set(snapshot)));
        }
        timeline.play();
    }

    private void animateCardsOnLoad() {
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
}
