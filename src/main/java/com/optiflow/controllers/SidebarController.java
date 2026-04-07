package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.utils.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SidebarController {

    private static final double EXPANDED_WIDTH = 220.0;
    private static final double COLLAPSED_WIDTH = 70.0;

    @FXML
    private VBox root;

    @FXML
    private VBox menuContainer;

    @FXML
    private Label logoLabel;

    @FXML
    private Button collapseBtn;

    private final List<MenuEntry> menuEntries = new ArrayList<>();
    private BiConsumer<String, String> navigationHandler;
    private boolean collapsed;
    private String activeKey;

    @FXML
    public void initialize() {
        renderMenuByRole();
    }

    public void setNavigationHandler(BiConsumer<String, String> handler) {
        this.navigationHandler = handler;
    }

    public void setActiveKey(String pageKey) {
        this.activeKey = pageKey;
        refreshActiveState();
    }

    @FXML
    private void toggleCollapse() {
        collapsed = !collapsed;
        double targetWidth = collapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;

        root.setPrefWidth(targetWidth);
        root.setMinWidth(targetWidth);
        root.setMaxWidth(targetWidth);

        logoLabel.setVisible(!collapsed);
        logoLabel.setManaged(!collapsed);
        collapseBtn.setText(collapsed ? "»" : "Collapse");

        for (MenuEntry entry : menuEntries) {
            entry.textLabel.setVisible(!collapsed);
            entry.textLabel.setManaged(!collapsed);
        }

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), menuContainer);
        slide.setFromX(collapsed ? 0 : -8);
        slide.setToX(collapsed ? -8 : 0);
        slide.play();
    }

    private void renderMenuByRole() {
        menuContainer.getChildren().clear();
        menuEntries.clear();

        User user = SessionManager.getUser();
        List<MenuItemConfig> configs = getConfigsForRole(user);

        for (MenuItemConfig config : configs) {
            MenuEntry entry = createMenuEntry(config);
            menuEntries.add(entry);
            menuContainer.getChildren().add(entry.node);
        }

        if (!configs.isEmpty()) {
            setActiveKey(configs.get(0).key);
            triggerNavigation(configs.get(0).key, configs.get(0).title);
        }
    }

    private MenuEntry createMenuEntry(MenuItemConfig config) {
        Label icon = new Label(config.icon);
        icon.getStyleClass().add("sidebar-item-icon");

        Label text = new Label(config.title);
        text.getStyleClass().add("sidebar-item-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox item = new HBox(10, icon, text, spacer);
        item.getStyleClass().add("sidebar-menu-item");
        if ("logout".equalsIgnoreCase(config.key)) {
            item.getStyleClass().add("sidebar-menu-logout");
        }

        item.setOnMouseClicked(event -> {
            setActiveKey(config.key);
            triggerNavigation(config.key, config.title);
        });

        item.setOnMouseEntered(event -> {
            playScale(item, 1.03);
        });

        item.setOnMouseExited(event -> {
            playScale(item, 1.0);
        });

        return new MenuEntry(config.key, item, text);
    }

    private void triggerNavigation(String key, String title) {
        if (navigationHandler != null) {
            navigationHandler.accept(key, title);
        }
    }

    private void refreshActiveState() {
        for (MenuEntry entry : menuEntries) {
            entry.node.getStyleClass().remove("sidebar-menu-active");
            if (entry.key.equalsIgnoreCase(activeKey)) {
                if (!entry.node.getStyleClass().contains("sidebar-menu-active")) {
                    entry.node.getStyleClass().add("sidebar-menu-active");
                }
            }
        }
    }

    private List<MenuItemConfig> getConfigsForRole(User user) {
        List<MenuItemConfig> configs = new ArrayList<>();

        if (user != null && user.isAdmin()) {
            configs.add(new MenuItemConfig("dashboard", "Dashboard", "⌂"));
            configs.add(new MenuItemConfig("projects", "Projects", "◻"));
            configs.add(new MenuItemConfig("tasks", "Tasks", "☑"));
            configs.add(new MenuItemConfig("managers", "Managers", "◍"));
            configs.add(new MenuItemConfig("employees", "Employees", "◉"));
            configs.add(new MenuItemConfig("worklogs", "Worklogs", "🗒"));
            configs.add(new MenuItemConfig("analytics", "Analytics", "◈"));
            configs.add(new MenuItemConfig("reports", "Reports", "⬙"));
            configs.add(new MenuItemConfig("settings", "Settings", "⚙"));
            configs.add(new MenuItemConfig("logout", "Logout", "⎋"));
            return configs;
        }

        if (user != null && user.isManager()) {
            configs.add(new MenuItemConfig("dashboard", "Dashboard", "⌂"));
            configs.add(new MenuItemConfig("team", "Team", "◑"));
            configs.add(new MenuItemConfig("tasks", "Tasks", "☑"));
            configs.add(new MenuItemConfig("assignment", "Task Assignment", "⇄"));
            configs.add(new MenuItemConfig("projects", "Projects", "◻"));
            configs.add(new MenuItemConfig("analytics", "Analytics", "◈"));
            configs.add(new MenuItemConfig("worklogs", "Worklogs", "🗒"));
            configs.add(new MenuItemConfig("logout", "Logout", "⎋"));
            return configs;
        }

        configs.add(new MenuItemConfig("dashboard", "Dashboard", "⌂"));
        configs.add(new MenuItemConfig("my_tasks", "My Tasks", "☑"));
        configs.add(new MenuItemConfig("task_detail", "Task Detail", "◧"));
        configs.add(new MenuItemConfig("worklogs", "Worklogs", "🗒"));
        configs.add(new MenuItemConfig("performance", "Performance", "⬡"));
        configs.add(new MenuItemConfig("logout", "Logout", "⎋"));
        return configs;
    }

    private void playScale(HBox node, double scale) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(130), node);
        transition.setToX(scale);
        transition.setToY(scale);
        transition.play();
    }

    private static class MenuItemConfig {
        final String key;
        final String title;
        final String icon;

        MenuItemConfig(String key, String title, String icon) {
            this.key = key;
            this.title = title;
            this.icon = icon;
        }
    }

    private static class MenuEntry {
        final String key;
        final HBox node;
        final Label textLabel;

        MenuEntry(String key, HBox node, Label textLabel) {
            this.key = key;
            this.node = node;
            this.textLabel = textLabel;
        }
    }
}
