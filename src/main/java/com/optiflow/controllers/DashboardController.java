package com.optiflow.controllers;

import com.optiflow.models.*;
import com.optiflow.services.*;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class DashboardController {
    private AuditLogService auditLogService;
    private WorkLogService workLogService;
    private TaskService taskService;
    private EmployeeService employeeService;
    private ProjectService projectService;
    public DashboardController(){
        this.auditLogService= new AuditLogService();
        this.workLogService= new WorkLogService();
        this.taskService=new TaskService();
        this.employeeService=new EmployeeService();
        this.projectService= new ProjectService();
    }
    @FXML
    private Label logsHeader;

    @FXML
    private VBox sidebarContainer;

    @FXML
    private VBox logsContainer;


    @FXML
    private Label badge;

    @FXML
    private HBox statsContainer;

    @FXML
    public void initialize() throws SQLException {
        User user = SessionManager.getUser();

        if (user == null) {
            System.out.println("No user logged in");
            return;
        }
        setBadge(user.getRole());
        if (user.isAdmin()) {
            logsHeader.setText("Audit Logs");
        } else if (user.isManager()) {
            logsHeader.setText("Work Logs");
        }else{
            logsHeader.setText("Your Tasks");
        }

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
            loadEmployeeTasks();
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
        try {
            List<Employee> emps = employeeService.getAllEmployees();
            int totEmp = emps.size();
            int active=0;
            int completed=0;
            List<Projects> pros = projectService.getAllProjects();
            for(Projects pro: pros){
                if ("active".equalsIgnoreCase(pro.getStatus())){
                    active+=1;
                }
                if ("completed".equalsIgnoreCase(pro.getStatus())){
                    completed+=1;
                }
            }
            int totPro = pros.size();

            statsContainer.getChildren().setAll(
                    createStatCard("Total Projects", String.valueOf(totPro), "#1E3A8A, #3B82F6"),
                    createStatCard("Active Projects", String.valueOf(active), "#065F46, #10B981"),
                    createStatCard("Previous Projects", String.valueOf(completed), "#92400E, #F59E0B"),
                    createStatCard("Total Employees", String.valueOf(totEmp), "#7F1D1D, #EF4444")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadAuditLogs() {
        logsContainer.getChildren().clear();

        try {
            List<AuditLog> logs = auditLogService.getAllLogs();

            for (AuditLog log : logs) {
                logsContainer.getChildren().add(createAuditLogCard(log));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ MANAGER SIDEBAR
    private void loadManagerSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(160);
        sidebar.setStyle("-fx-background-color: #111827; -fx-padding: 15;");

        Button analyticsBtn = createButton("Analytics", true);
        Button projectBtn = createButton("View Project", false);
        Button tasksBtn = createButton("Tasks", false);
        Button empBtn = createButton("Employees", false);

        // 🔥 NAVIGATION
        tasksBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/optiflow/views/tasks.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) tasksBtn.getScene().getWindow();
                stage.setScene(new Scene(root));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sidebar.getChildren().addAll(
                analyticsBtn,
                projectBtn,
                tasksBtn,
                empBtn,
                createLogoutButton()
        );

        sidebarContainer.getChildren().setAll(sidebar);
    }
    private void loadManagerStats() throws SQLException {
        User user = SessionManager.getUser();
        int mid= user.getUserId();
        List<Employee> emps = employeeService.getEmployeesByManager(mid);
        int totEmp = emps.size();
        List<Projects> pros = projectService.getAllProjects();
        int totPro = pros.size();
        int pid=projectService.getProjectByManager(mid);
        List<Tasks> tasks = projectService.getTasksByProject(pid);
        int totTasks=tasks.size();
        int active=0;
        for(Tasks task: tasks){
            if ("active".equalsIgnoreCase(task.getStatus())){
                active+=1;
            }

            }



        statsContainer.getChildren().setAll(
                createStatCard("Total Projects", String.valueOf(totPro), "#1E3A8A, #3B82F6"),
                createStatCard("Employees", String.valueOf(totEmp), "#065F46, #10B981"),
                createStatCard("Total Tasks", String.valueOf(totTasks), "#92400E, #F59E0B"),
                createStatCard("Completed Tasks", String.valueOf(active), "#7F1D1D, #EF4444")
        );
    }
    private void loadWorkLogs() throws SQLException {
        logsContainer.getChildren().clear();
        User user = SessionManager.getUser();
        int Mid=user.getUserId();
        int Pid=projectService.getProjectByManager(Mid);

        try {
            List<WorkLog> logs = workLogService.getLogsByProject(Pid); // or all logs method

            for (WorkLog log : logs) {
                logsContainer.getChildren().add(createWorkLogCard(log));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void loadEmployeeTasks() {
        logsContainer.getChildren().clear(); // reuse same container

        try {
            int empId = SessionManager.getUser().getUserId();

            // 👉 YOU will replace this with your service call
            List<Tasks> tasks = taskService.getTaskByEmp(empId);

            for (Tasks task : tasks) {
                logsContainer.getChildren().add(createTaskCard(task));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private VBox createWorkLogCard(WorkLog log) {

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;"
        );

        // TOP ROW: Employee + Task + Date
        HBox top = new HBox();

        Label title = new Label("Emp #" + log.getEmployeeId() + " | Task #" + log.getTaskId());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label date = new Label(String.valueOf(log.getWorkDate()));
        date.setStyle("-fx-text-fill: #a0a5b0;");

        top.getChildren().addAll(title, spacer, date);

        // HOURS ROW
        HBox mid = new HBox(5);

        Label hours = new Label("Hours: " + log.getHoursWorked());
        hours.setStyle("-fx-text-fill: white;");

        mid.getChildren().add(hours);

        // DESCRIPTION
        Label desc = new Label(log.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #6B7280;");

        card.getChildren().addAll(top, mid, desc);

        return card;
    }
    private VBox createAuditLogCard(AuditLog log) {

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;"
        );

        // TOP ROW: Entity Type + Entity ID + Date
        HBox top = new HBox();

        Label entity = new Label(log.getEntityType() + " #" + log.getEntity_id());
        entity.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label date = new Label(String.valueOf(log.getDate()));
        date.setStyle("-fx-text-fill: #a0a5b0;");

        top.getChildren().addAll(entity, spacer, date);

        // USER ROW
        HBox userRow = new HBox(5);

        Label user = new Label("User ID: " + log.getUser_id());
        user.setStyle("-fx-text-fill: white;");

        userRow.getChildren().add(user);

        // DESCRIPTION
        Label desc = new Label(log.getDetails());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #6B7280;");

        card.getChildren().addAll(top, userRow, desc);

        return card;
    }
    private VBox createTaskCard(Tasks task) {

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 4);"
        );

        // 🔹 TOP ROW (ONLY TITLE)
        HBox top = new HBox();

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        top.getChildren().add(title);

        // 🔹 DESCRIPTION
        Label desc = new Label(task.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #6B7280;");

        // 🔹 OPEN BUTTON
        Button openBtn = new Button("Open Task");
        openBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #065F46, #10B981);" +
                        "-fx-text-fill: white;"
        );

        // 👉 NAVIGATION TO viewTasks.fxml
        openBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/optiflow/views/viewTasks.fxml"));
                Parent root = loader.load();

                // OPTIONAL: pass task to next controller
                ViewTasksController controller = loader.getController();
                controller.setTask(task);

                Stage stage = (Stage) openBtn.getScene().getWindow();
                stage.setScene(new Scene(root));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        card.getChildren().addAll(top, desc, openBtn);

        return card;
    }
}