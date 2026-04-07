package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.services.AuthService;
import com.optiflow.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    private AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Label LoginerrorLabel;

    // 🔐 LOGIN LOGIC
    @FXML
    private void handleLogin(ActionEvent event) {

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            if (LoginerrorLabel != null) {
                LoginerrorLabel.setText("Please fill all fields");
            }
            return;
        }

        try {
            User user = authService.login(email, password);

            if (user!=null) {
                SessionManager.setUser(user);

                Parent root = FXMLLoader.load(getClass().getResource("/gui/DashboardLayout.fxml"));

                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                // maximizeStage(stage);
                stage.setMaximized(true);

            } else {
                if (LoginerrorLabel != null) {
                    LoginerrorLabel.setText("Invalid credentials or DB not configured");
                }
            }
        } catch (Exception ex) {
            if (LoginerrorLabel != null) {
                LoginerrorLabel.setText("Login failed. Check DB configuration.");
            }
        }
    }

    // 🔁 GO TO REGISTER
    @FXML
    private void goToRegister(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/gui/Register.fxml"));

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        // maximizeStage(stage);
        stage.setMaximized(true);
    }

    private void maximizeStage(Stage stage) {
        if (stage == null) {
            return;
        }

        stage.setResizable(true);
        stage.setMaximized(true);
    }
}