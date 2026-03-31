package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    private AuthService authService;

    public RegisterController() {
        this.authService = new AuthService();
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label RegistererrorLabel;

    @FXML
    private void handleRegister(ActionEvent event) throws SQLException, IOException {

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        RegistererrorLabel.setText("");


        // 🔒 Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            RegistererrorLabel.setText("Please fill all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            RegistererrorLabel.setText("Passwords do not match");
            return;
        }

        boolean checkEmail = authService.isValidEmail(email);
        if (!checkEmail) {
            RegistererrorLabel.setText("Invalid email format");
            return;
        }

        boolean checkPass = authService.validatePassword(password);
        if (!checkPass) {
            RegistererrorLabel.setText("Invalid password format");
            return;
        }

        // 🚀 Create user
        User u1 = new User(username, email, password, "employee");

        boolean registerCheck = authService.register(u1);

        if (registerCheck) {
            System.out.println("Registration Done");

            // ✅ Load Login Page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            System.out.println("Registration failed");
        }
    }

    // 🔁 OPTIONAL: for "Already have account? Login"
    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        System.out.println(getClass().getResource("/fxml/login.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}