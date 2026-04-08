package com.optiflow.controllers;

import com.optiflow.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TopBarController {

    @FXML
    private HBox searchContainer;

    @FXML
    private TextField searchField;

    @FXML
    private StackPane notificationButton;

    @FXML
    private HBox profileTrigger;

    @FXML
    private VBox profileDropdown;

    @FXML
    private Label notificationBadgeLabel;

    @FXML
    private Label profileInitialsLabel;

    @FXML
    private Label profileNameLabel;

    private boolean dropdownOpen;

    @FXML
    public void initialize() {
        profileDropdown.setVisible(false);
        profileDropdown.setManaged(false);
        syncProfileIdentity();

        searchField.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (focused) {
                if (!searchContainer.getStyleClass().contains("topbar-search-focused")) {
                    searchContainer.getStyleClass().add("topbar-search-focused");
                }
            } else {
                searchContainer.getStyleClass().remove("topbar-search-focused");
            }
        });
    }

    @FXML
    private void handleNotificationHoverIn(MouseEvent event) {
        playScale(notificationButton, 1.10);
    }

    @FXML
    private void handleNotificationHoverOut(MouseEvent event) {
        playScale(notificationButton, 1.0);
    }

    @FXML
    private void handleNotificationClick(MouseEvent event) {
        notificationBadgeLabel.setText("0");
    }

    @FXML
    private void handleProfileHoverIn(MouseEvent event) {
        playScale(profileTrigger, 1.04);
    }

    @FXML
    private void handleProfileHoverOut(MouseEvent event) {
        playScale(profileTrigger, 1.0);
    }

    @FXML
    private void toggleProfileMenu(MouseEvent event) {
        if (dropdownOpen) {
            closeDropdown();
        } else {
            openDropdown();
        }
    }

    @FXML
    private void handleProfileAction() {
        closeDropdown();
    }

    @FXML
    private void handleLogoutAction() {
        SessionManager.setUser(null);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
            Stage stage = (Stage) profileTrigger.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.setMaximized(true);
        } catch (Exception ignored) {
        }
        closeDropdown();
    }

    private void syncProfileIdentity() {
        var user = SessionManager.getUser();
        String displayName = user != null && user.getName() != null && !user.getName().isBlank()
                ? user.getName().trim()
                : "Guest";
        String initials = buildInitials(displayName);

        if (profileNameLabel != null) {
            profileNameLabel.setText(displayName);
        }

        if (profileInitialsLabel != null) {
            profileInitialsLabel.setText(initials);
        }
    }

    private String buildInitials(String displayName) {
        String[] parts = displayName.split("\\s+");
        if (parts.length == 0) {
            return "?";
        }

        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!part.isBlank()) {
                builder.append(Character.toUpperCase(part.charAt(0)));
            }
            if (builder.length() == 2) {
                break;
            }
        }

        return builder.length() == 0 ? "?" : builder.toString();
    }

    private void openDropdown() {
        dropdownOpen = true;
        profileDropdown.setManaged(true);
        profileDropdown.setVisible(true);
        profileDropdown.setOpacity(0);
        profileDropdown.setTranslateY(32);

        FadeTransition fade = new FadeTransition(Duration.millis(220), profileDropdown);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), profileDropdown);
        slide.setFromY(32);
        slide.setToY(44);

        new ParallelTransition(fade, slide).play();
    }

    private void closeDropdown() {
        dropdownOpen = false;

        FadeTransition fade = new FadeTransition(Duration.millis(180), profileDropdown);
        fade.setFromValue(profileDropdown.getOpacity());
        fade.setToValue(0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(180), profileDropdown);
        slide.setFromY(profileDropdown.getTranslateY());
        slide.setToY(32);

        ParallelTransition out = new ParallelTransition(fade, slide);
        out.setOnFinished(e -> {
            profileDropdown.setVisible(false);
            profileDropdown.setManaged(false);
        });
        out.play();
    }

    private void playScale(Node node, double toScale) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(140), node);
        scale.setToX(toScale);
        scale.setToY(toScale);
        scale.play();
    }
}
