package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.EmployeeService;
import com.optiflow.services.ProjectService;
import com.optiflow.services.ReferenceDataService;
import com.optiflow.services.TaskService;
import com.optiflow.services.WorkLogService;
import com.optiflow.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    private final Map<String, Node> pageCache = new HashMap<>();
    private final ProjectService projectService = new ProjectService();
    private final TaskService taskService = new TaskService();
    private final EmployeeService employeeService = new EmployeeService();
    private final WorkLogService workLogService = new WorkLogService();
    private final ReferenceDataService referenceDataService = new ReferenceDataService();

    @FXML
    private Label roleBadge;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label sectionTitle;

    @FXML
    private StackPane centerContainer;

    @FXML
    private VBox sidebarComponent;

    @FXML
    private SidebarController sidebarComponentController;

    @FXML
    private StackPane notificationButton;

    @FXML
    private Label notificationBadgeLabel;

    @FXML
    public void initialize() {
        applyCurrentUserMeta();
        wireSidebar();
        configureNotificationBell();
    }

    public void setCenterContent(Node node) {
        if (centerContainer == null || node == null) {
            return;
        }

        node.setOpacity(0);
        node.setTranslateY(10);
        centerContainer.getChildren().setAll(node);

        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
        slide.setFromY(10);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();
    }

    private void applyCurrentUserMeta() {
        User user = SessionManager.getUser();
        String role = user != null && user.getRole() != null ? user.getRole() : "User";
        String name = user != null && user.getName() != null ? user.getName() : "Team Member";

        if (roleBadge != null) {
            roleBadge.setText(role.toUpperCase() + " VIEW");
        }
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + name);
        }
    }

    private void wireSidebar() {
        if (sidebarComponentController == null) {
            return;
        }

        sidebarComponentController.setNavigationHandler(this::handleNavigation);
        sidebarComponentController.setActiveKey("dashboard");
        handleNavigation("dashboard", "Dashboard");
    }

    private void handleNavigation(String pageKey, String title) {
        if ("logout".equals(pageKey)) {
            performLogout();
            return;
        }

        Node page = pageCache.computeIfAbsent(pageKey, this::buildPageContent);
        sectionTitle.setText(title);
        setCenterContent(page);
    }

    private void configureNotificationBell() {
        if (notificationBadgeLabel == null) {
            return;
        }

        notificationBadgeLabel.setVisible(true);
        notificationBadgeLabel.setManaged(true);
    }

    @FXML
    private void handleNotificationHoverIn() {
        if (notificationButton != null) {
            notificationButton.setScaleX(1.08);
            notificationButton.setScaleY(1.08);
        }
    }

    @FXML
    private void handleNotificationHoverOut() {
        if (notificationButton != null) {
            notificationButton.setScaleX(1.0);
            notificationButton.setScaleY(1.0);
        }
    }

    @FXML
    private void handleNotificationClick() {
        if (notificationBadgeLabel != null) {
            notificationBadgeLabel.setText("0");
        }
        handleNavigation("notifications", "Notifications");
    }

    private void performLogout() {
        try {
            SessionManager.setUser(null);
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
            Stage stage = (Stage) centerContainer.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setResizable(true);
            stage.setMaximized(true);
        } catch (Exception ignored) {
        }
    }

    private Node buildPageContent(String key) {
        User user = SessionManager.getUser();
        boolean managerOnlyRestricted = user != null && user.isManager()
            && ("managers".equals(key)
            || "view_managers".equals(key)
            || "project_form".equals(key)
            || "add_projects".equals(key));

        if (managerOnlyRestricted) {
            return buildCardGrid(
                createMetricCard("Access Restricted", "Managers can only view assigned projects", "Project creation is limited to admin users"),
                createMetricCard("Allowed", "Dashboard, Projects, Tasks", "Use assigned modules from sidebar"),
                createMetricCard("Policy", "Role-based visibility", "Other managers' data is hidden")
            );
        }

        switch (key) {
            case "dashboard":
            case "overview_dashboard":
                return loadRoleDashboard();
            case "team":
                return loadView("EmployeeList.fxml");
            case "managers":
                return loadView("Managers.fxml");
            case "view_managers":
                return loadView("ViewManagers.fxml");
            case "projects":
                return loadView("ProjectList.fxml");
            case "view_projects":
                return loadView("ProjectDetail.fxml");
            case "add_projects":
            case "project_form":
                return loadView("ProjectForm.fxml");
            case "project_detail":
                return loadView("ProjectDetail.fxml");
            case "tasks":
            case "my_tasks":
                return loadView("TaskList.fxml");
            case "view_tasks":
                return loadView("TaskDetail.fxml");
            case "add_tasks":
            case "task_detail":
                return loadView("TaskDetail.fxml");
            case "assignment":
            case "allocation":
                return loadView("TaskAssignment.fxml");
            case "employees":
                return loadView("EmployeeList.fxml");
            case "employee_profile":
            case "performance":
                return loadView("EmployeeProfile.fxml");
            case "analytics":
                return loadView("Analytics.fxml");
            case "reports":
                return loadView("ManagerDashboard.fxml");
            case "comments":
                return loadView("TaskDetail.fxml");
            case "add_worklogs":
            case "worklogs":
                return loadView("WorklogsPanel.fxml");
            case "notifications":
                return loadView("NotificationPanel.fxml");
            case "overview":
            default:
                return buildOverviewCards();
        }
    }

    private Node loadRoleDashboard() {
        User user = SessionManager.getUser();
        if (user != null && user.isAdmin()) {
            return loadView("AdminDashboard.fxml");
        }
        if (user != null && user.isManager()) {
            return loadView("ManagerDashboard.fxml");
        }
        return loadView("EmployeeDashboard.fxml");
    }

    private Node loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/" + fxmlFile));
            Node content = loader.load();

            if (content instanceof ScrollPane) {
                return content;
            }

            if (content instanceof Region region) {
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(false);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setPannable(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
            scrollPane.getStyleClass().add("content-scroll-wrap");
            scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            return scrollPane;
        } catch (Exception ex) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            ex.printStackTrace();
            Throwable cause = ex.getCause();
            String causeMsg = (cause != null && cause.getMessage() != null) ? cause.getMessage() : null;
            String errorMessage = ex.getMessage() == null ? "Unknown error" : ex.getMessage();
            if (causeMsg != null && !causeMsg.isBlank()) {
                errorMessage = errorMessage + " | Cause: " + causeMsg;
            }

            return buildCardGrid(
                    createMetricCard("Unable to load page", fxmlFile, "Please check FXML/controller configuration"),
                    createMetricCard("Error", ex.getClass().getSimpleName(), errorMessage),
                    createMetricCard("Fallback", "Static dashboard view", "App remains functional")
            );
        }
    }

    private VBox buildCardGrid(Node... cards) {
        VBox wrap = new VBox(16);
        wrap.getStyleClass().add("dash-center-wrap");

        HBox topRow = new HBox(14);
        HBox bottomRow = new HBox(14);
        topRow.getStyleClass().add("dash-card-row");
        bottomRow.getStyleClass().add("dash-card-row");

        for (int i = 0; i < cards.length; i++) {
            if (i < 2) {
                topRow.getChildren().add(cards[i]);
            } else {
                bottomRow.getChildren().add(cards[i]);
            }
        }

        wrap.getChildren().add(topRow);
        if (!bottomRow.getChildren().isEmpty()) {
            wrap.getChildren().add(bottomRow);
        }

        return wrap;
    }

    private VBox createMetricCard(String title, String value, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dash-card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("dash-card-value");

        Label subLabel = new Label(subtitle);
        subLabel.getStyleClass().add("dash-card-sub");

        VBox card = new VBox(8, titleLabel, valueLabel, subLabel);
        card.getStyleClass().add("dash-surface-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private Node buildOverviewCards() {
        User user = SessionManager.getUser();
        if (user == null) {
            return buildCardGrid(
                    createMetricCard("Workspace", "Guest", "Sign in to view live metrics"),
                    createMetricCard("Projects", "0", "No session available"),
                    createMetricCard("Tasks", "0", "No session available")
            );
        }

        try {
            if (user.isAdmin()) {
                List<?> projects = projectService.getAllProjects();
                List<?> tasks = taskService.getAllTasks();
                List<?> employees = employeeService.getAllEmployees();

                return buildCardGrid(
                        createMetricCard("Projects", String.valueOf(projects == null ? 0 : projects.size()), "All active projects"),
                        createMetricCard("Tasks", String.valueOf(tasks == null ? 0 : tasks.size()), "All tracked tasks"),
                        createMetricCard("Employees", String.valueOf(employees == null ? 0 : employees.size()), "Current workforce")
                );
            }

            Employee employee = employeeService.getEmployeeByUserId(user.getUserId());
            if (employee == null) {
                return buildCardGrid(
                        createMetricCard("Profile", user.getName(), "No employee profile linked"),
                        createMetricCard("Tasks", "0", "No assigned tasks"),
                        createMetricCard("Worklogs", "0h", "No tracked hours")
                );
            }

            List<?> assignedTasks = taskService.getTaskByEmp(employee.getEmp_id());
            int totalTasks = assignedTasks == null ? 0 : assignedTasks.size();
            int completedTasks = 0;
            if (assignedTasks != null) {
                for (Object object : assignedTasks) {
                    Tasks task = (Tasks) object;
                    if (task.getStatus() != null && task.getStatus().equalsIgnoreCase("completed")) {
                        completedTasks++;
                    }
                }
            }

            int totalHours = workLogService.getTotalHoursWorked(employee.getEmp_id());
            String projectLabel = user.isManager() ? "Team scope" : "My workload";

            return buildCardGrid(
                    createMetricCard(projectLabel, String.valueOf(totalTasks), "Assigned tasks"),
                    createMetricCard("Completed", String.valueOf(completedTasks), "Closed items"),
                    createMetricCard("Hours", totalHours + "h", referenceDataService.getDefaultTaskStatus() + " log baseline")
            );
        } catch (Exception ex) {
            return buildCardGrid(
                    createMetricCard("Overview", "Unavailable", ex.getClass().getSimpleName()),
                    createMetricCard("Fallback", "Active", "Could not read live metrics"),
                    createMetricCard("Session", user.getRole(), user.getName())
            );
        }
    }

}