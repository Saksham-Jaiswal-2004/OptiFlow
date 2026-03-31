package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.services.AuthService;
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
    private void handleLogin(ActionEvent event) throws IOException, SQLException {

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            LoginerrorLabel.setText("Please fill all fields");
            return;
        }

        User user = authService.login(email, password);

        if (user!=null) {
            System.out.println("Login successful");

            // 👉 go to dashboard (change path if needed)
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } else {
            LoginerrorLabel.setText("Invalid credentials");
        }
    }

    // 🔁 GO TO REGISTER
    @FXML
    private void goToRegister(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}