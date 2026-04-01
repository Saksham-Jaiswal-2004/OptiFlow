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
import javafx.stage.Stage;

import java.util.List;

public class ManagerController {

    @FXML private VBox managersContainer;
    @FXML private Label roleBadge;
    @FXML private Button analyticsBtn;

    private EmployeeService employeeService = new EmployeeService();
    private ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        try {
            User user = SessionManager.getUser();
            if (user == null) return;

            setRoleBadge(user.getRole());
            loadManagers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 ROLE BADGE
    private void setRoleBadge(String role) {
        roleBadge.setText(role.toUpperCase());

        roleBadge.setStyle(
                "-fx-background-color: linear-gradient(to right, #8e2de2, #c471ed);" +
                        "-fx-background-radius: 50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10;"
        );
    }

    // 🔹 LOAD MANAGERS
    private void loadManagers() {
        managersContainer.getChildren().clear();

        try {
            List<Employee> managers = employeeService.getAllManagers();

            for (Employee manager : managers) {
                managersContainer.getChildren().add(createManagerCard(manager));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 CARD UI
    private VBox createManagerCard(Employee manager) {

        VBox card = new VBox(12);
        card.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-padding: 18;" +
                        "-fx-background-radius: 10;"
        );

        // 🔹 TOP ROW
        HBox top = new HBox(10);

        Label name = new Label(manager.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label divider = new Label("|");
        divider.setStyle("-fx-text-fill: #9CA3AF;");

        String projectName = "No Project";
        try {
            int pid = projectService.getProjectByManager(manager.getEmp_id());
            Projects p= projectService.getProjectById(pid);
            if (p != null) projectName = p.getName();
        } catch (Exception ignored) {}

        Label project = new Label(projectName);
        project.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(name, divider, project, spacer);

        // 🔹 DETAILS
        VBox details = new VBox(6);

        Label id = new Label("ID: " + manager.getEmp_id());
        id.setStyle("-fx-text-fill: white;");

        int empCount = 0;
        try {
            empCount = employeeService.getEmployeesByManager(manager.getEmp_id()).size();
        } catch (Exception ignored) {}

        Label count = new Label("Number Of Employees: " + empCount);
        count.setStyle("-fx-text-fill: white;");

        details.getChildren().addAll(id, count);

        // 🔹 BUTTON
        Button viewBtn = new Button("View Details");
        viewBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #065F46, #10B981);" +
                        "-fx-text-fill: white;"
        );

        // 👉 You can later navigate to manager detail page
        viewBtn.setOnAction(e -> {
            System.out.println("View Manager " + manager.getName());
        });

        card.getChildren().addAll(top, details, viewBtn);

        return card;
    }
    @FXML
    private void handleAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/optiflow/views/dashboard.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) analyticsBtn.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
