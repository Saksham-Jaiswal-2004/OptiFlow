package com.optiflow.controllers;

import com.optiflow.models.*;
import com.optiflow.services.*;
import com.optiflow.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class TaskController {


    @FXML private Label roleBadge;
    @FXML private HBox statsContainer;
    @FXML private VBox tasksContainer;

    @FXML private Button exportBtn;
    @FXML private Button addBtn;


    private TaskService taskService = new TaskService();
    private ProjectService projectService = new ProjectService();
    private EmployeeService employeeService = new EmployeeService();


    private int managerId;
    private int projectId;


    @FXML
    public void initialize() {
        try {
            User user = SessionManager.getUser();
            if (user == null) return;

            managerId = user.getUserId();
            projectId = projectService.getProjectByManager(managerId);

            setRoleBadge(user.getRole());

            loadStats();
            loadTasks();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setRoleBadge(String role) {
        roleBadge.setText(role.toUpperCase());

        roleBadge.setStyle(
                "-fx-background-color: linear-gradient(to right, #8e2de2, #c471ed);" +
                        "-fx-background-radius: 50;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5 10;"
        );
    }


    private void loadStats() {
        try {
            List<Tasks> tasks = taskService.getTaskByProject(projectId);
            int total = tasks.size();

            int completed = 0;
            for (Tasks t : tasks) {
                if ("completed".equalsIgnoreCase(t.getStatus())) {
                    completed++;
                }
            }

            List<Employee> emps = employeeService.getEmployeesByManager(managerId);

            statsContainer.getChildren().setAll(
                    createStatCard("Total Tasks", String.valueOf(total), "#1E3A8A, #3B82F6"),
                    createStatCard("Completed Tasks", String.valueOf(completed), "#065F46, #10B981"),
                    createStatCard("Total Employees", String.valueOf(emps.size()), "#7F1D1D, #EF4444")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadTasks() {
        tasksContainer.getChildren().clear();

        try {
            List<Tasks> tasks = taskService.getTaskByProject(projectId);

            for (Tasks task : tasks) {
                tasksContainer.getChildren().add(createTaskCard(task));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private VBox createTaskCard(Tasks task) {

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;"
        );


        HBox top = new HBox(10);

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label status = new Label(task.getStatus());
        status.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-padding: 3 8;");

        Label assigned = new Label("Assigned");
        assigned.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-padding: 3 8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white;");


        editBtn.setOnAction(e -> openTask(task, true));

        top.getChildren().addAll(title, status, assigned, spacer, editBtn);


        Label desc = new Label(task.getDescription());
        desc.setStyle("-fx-text-fill: #9CA3AF;");
        desc.setWrapText(true);


        Button openBtn = new Button("Open Task");
        openBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white;");


        openBtn.setOnAction(e -> openTask(task, false));

        card.getChildren().addAll(top, desc, openBtn);

        return card;
    }


    private void openTask(Tasks task, boolean editable) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/optiflow/views/viewTasks.fxml"));
            Parent root = loader.load();

            ViewTasksController controller = loader.getController();
            controller.setTask(task);


            if (editable) {
                controller.enableEditMode();
            }

            Stage stage = (Stage) tasksContainer.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/optiflow/views/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tasksContainer.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 🔹 EXPORT CSV

    @FXML
    private void handleExport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Tasks CSV");


            fileChooser.setInitialFileName("tasks_" + java.time.LocalDate.now() + ".csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            Stage stage = (Stage) tasksContainer.getScene().getWindow();

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                String filePath = file.getAbsolutePath();

                String savedFile = taskService.exportTasksToCSV(projectId, filePath);

                System.out.println("Saved to: " + savedFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private VBox createStatCard(String title, String value, String gradient) {
        VBox box = new VBox(5);

        box.setStyle(
                "-fx-background-radius: 12;" +
                        "-fx-padding: 18;" +
                        "-fx-background-color: linear-gradient(to right, " + gradient + ");"
        );

        Label t = new Label(title);
        t.setStyle("-fx-text-fill: white;");

        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        box.getChildren().addAll(t, v);
        return box;
    }
}