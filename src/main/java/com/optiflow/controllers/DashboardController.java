package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class DashboardController {

    private final Map<String, Node> pageCache = new HashMap<>();

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
    public void initialize() {
        applyCurrentUserMeta();
        wireSidebar();
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
        Node page = pageCache.computeIfAbsent(pageKey, this::buildPageContent);
        sectionTitle.setText(title);
        setCenterContent(page);
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
                return loadView("CommentsPanel.fxml");
            case "add_worklogs":
            case "worklogs":
                return loadView("WorklogsPanel.fxml");
            case "notifications":
                return loadView("NotificationPanel.fxml");
            case "settings":
                return buildCardGrid(
                        createMetricCard("Workspace", "OptiFlow SME", "Production environment"),
                        createMetricCard("Access Policies", "14", "2 pending review"),
                        createMetricCard("Audit Mode", "Enabled", "Realtime logs active")
                );
            case "overview":
            default:
                return buildCardGrid(
                        createMetricCard("Resource Utilization", "84%", "Healthy allocation mix"),
                        createMetricCard("Open Requests", "12", "4 high-priority"),
                        createMetricCard("Project Health", "B+", "2 teams need support")
                );
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
            return buildCardGrid(
                    createMetricCard("Unable to load page", fxmlFile, "Please check FXML/controller configuration"),
                    createMetricCard("Error", ex.getClass().getSimpleName(), ex.getMessage() == null ? "Unknown error" : ex.getMessage()),
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

}