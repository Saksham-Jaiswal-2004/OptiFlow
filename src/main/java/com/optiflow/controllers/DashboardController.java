package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.services.AuditLogService;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class DashboardController {
    private AuditLogService auditLogService;

    public DashboardController(){
        this.auditLogService= new AuditLogService();
    }

    @FXML
    private VBox sidebarContainer;

    @FXML
    private VBox logsContainer;


    @FXML
    private Label badge;

    @FXML
    private HBox statsContainer;

    @FXML
    public void initialize() {
        User user = SessionManager.getUser();

        if (user == null) {
            System.out.println("No user logged in");
            return;
        }
        setBadge(user.getRole());

        if (user.isAdmin()) {
            loadAdminSidebar();
            loadAdminStats();
            loadAuditLogs();

        }
        else if (user.isManager()) {
            loadManagerSidebar();

            loadManagerStats();
            loadWorkLogs();
        }
        else if (user.isEmployee()) {
            loadEmployeeSidebar();
            loadEmployeeStats();

        }
    }

    // ✅ ADMIN SIDEBAR
    private void loadAdminSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(160);
        sidebar.setStyle("-fx-background-color: #111827; -fx-padding: 15;");

        sidebar.getChildren().addAll(
                createButton("Analytics", true),
                createButton("Managers", false),
                createButton("Projects", false),
                createLogoutButton()
        );

        sidebarContainer.getChildren().setAll(sidebar);
    }
    private void loadAdminStats() {
        statsContainer.getChildren().setAll(

                createStatCard("Total Projects", "10", "#1E3A8A, #3B82F6"),
                createStatCard("Active Projects", "5", "#065F46, #10B981"),
                createStatCard("Previous Projects", "50", "#92400E, #F59E0B"),
                createStatCard("Cancelled Projects", "10", "#7F1D1D, #EF4444")
        );
    }
    private void loadAuditLogs(){

    }

    // ✅ MANAGER SIDEBAR
    private void loadManagerSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(160);
        sidebar.setStyle("-fx-background-color: #111827; -fx-padding: 15;");

        sidebar.getChildren().addAll(
                createButton("Analytics", true),
                createButton("View Project", false),
                createButton("Tasks", false),
                createButton("Employees", false),
                createLogoutButton()
        );

        sidebarContainer.getChildren().setAll(sidebar);
    }
    private void loadManagerStats() {
        statsContainer.getChildren().setAll(
                createStatCard("Total Projects", "8", "#1E3A8A, #3B82F6"),
                createStatCard("Employees", "12", "#065F46, #10B981"),
                createStatCard("Total Tasks", "40", "#92400E, #F59E0B"),
                createStatCard("Completed Tasks", "30", "#7F1D1D, #EF4444")
        );
    }

    // ✅ EMPLOYEE SIDEBAR
    private void loadEmployeeSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(160);
        sidebar.setStyle("-fx-background-color: #111827; -fx-padding: 15;");

        sidebar.getChildren().addAll(
                createButton("Tasks", true),
                createButton("Work Log", false),
                createLogoutButton()
        );

        sidebarContainer.getChildren().setAll(sidebar);
    }
    private void loadEmployeeStats() {
        statsContainer.getChildren().clear(); // nothing shown
    }

    // 🔹 COMMON BUTTON
    private Button createButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);

        String style = active
                ? "-fx-background-color: #374151; -fx-text-fill: white;"
                : "-fx-background-color: transparent; -fx-text-fill: #D1D5DB;";

        btn.setStyle(style +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 10 15;" +
                "-fx-background-radius: 8;");

        return btn;
    }

    // 🔹 LOGOUT BUTTON
    private Button createLogoutButton() {
        Button btn = new Button("Logout");
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #EF4444;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 10 15;"
        );

        VBox.setMargin(btn, new Insets(150, 0, 0, 0));

        return btn;
    }
    private void setBadge(String role) {
        badge.setText(role.toUpperCase());

        String color;

        switch (role.toLowerCase()) {
            case "admin":
                color = "#8e2de2, #c471ed";
                break;
            case "manager":
                color = "#11998e, #38ef7d";
                break;
            case "employee":
                color = "#f12711, #f5af19";
                break;
            default:
                color = "#6B7280, #6B7280";
        }

        badge.setStyle(
                "-fx-background-color: linear-gradient(to right, " + color + ");" +
                        "-fx-background-radius: 50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10;"
        );
    }
    private VBox createStatCard(String title, String value, String gradient) {
        VBox box = new VBox(5);

        box.setStyle(
                "-fx-background-radius: 12;" +
                        "-fx-padding: 18;" +
                        "-fx-background-color: linear-gradient(to right, " + gradient + ");"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        box.getChildren().addAll(titleLabel, valueLabel);
        HBox.setHgrow(box, javafx.scene.layout.Priority.ALWAYS);

        return box;
    }
    private VBox createLogCard(String project, String user, String role, String date, String desc) {

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;"
        );

        // TOP ROW
        HBox top = new HBox();

        Label projectLabel = new Label(project);
        projectLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-text-fill: #a0a5b0;");

        top.getChildren().addAll(projectLabel, spacer, dateLabel);

        // USER ROW
        HBox userRow = new HBox(5);

        Label userLabel = new Label(user);
        userLabel.setStyle("-fx-text-fill: white;");

        Label roleLabel = new Label("• " + role);
        roleLabel.setStyle("-fx-text-fill: #a0a5b0;");

        userRow.getChildren().addAll(userLabel, roleLabel);

        // DESCRIPTION
        Label descLabel = new Label(desc);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #6B7280;");

        card.getChildren().addAll(top, userRow, descLabel);

        return card;
    }

}