package com.optiflow.controllers;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;
import com.optiflow.services.AuthService;
import com.optiflow.utils.AppContext;
import com.optiflow.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

public class AuthController
{
    private AuthService authService;

    public AuthController()
    {
        this.authService = new AuthService();
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label LoginerrorLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label RegistererrorLabel;

    UserDAO userDAO = new UserDAO();

    @FXML
    public boolean handleLogin(ActionEvent event)
    {
        String email = emailField.getText();
        String password = passwordField.getText();

        try
        {
            if (email.isEmpty() || password.isEmpty())
            {
                LoginerrorLabel.setText("Please fill all fields");
                return false;
            }

            User user = authService.login(email, password);

            if(user != null)
            {
                LoginerrorLabel.setText("Login successful");

                SessionManager.setUser(user);
                AppContext.initSocket();

                Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));

                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));

                return true;
            }
            else
            {
                LoginerrorLabel.setText("Invalid credentials");
                return false;
            }

        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void handleLogout()
    {
        SessionManager.setUser(null);
    }

    public boolean handleRegister(ActionEvent event) throws SQLException, IOException {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        RegistererrorLabel.setText("");

        if (username.isEmpty() || email.isEmpty() || password.isEmpty())
        {
            RegistererrorLabel.setText("Please fill all fields");
            return false;
        }

        if (!password.equals(confirmPassword))
        {
            RegistererrorLabel.setText("Passwords do not match");
            return false;
        }

        boolean checkEmail = authService.isValidEmail(email);
        if (!checkEmail)
        {
            RegistererrorLabel.setText("Invalid email format");
            return false;
        }

        boolean checkPass = authService.validatePassword(password);
        if (!checkPass)
        {
            RegistererrorLabel.setText("Invalid password format");
            return false;
        }

        User u1 = new User(username, email, password, "employee");

        boolean registerCheck = authService.register(u1);

        if (registerCheck)
        {
            System.out.println("Registration Done");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

            SessionManager.setUser(u1);
            AppContext.initSocket();

            return true;
        }
        else
        {
            System.out.println("Registration failed");
            return false;
        }
    }

    public boolean handlePasswordChange(int userId, String oldPassword, String newPassword)
    {
        return true;
    }

    public boolean handlePasswordReset(String email)
    {
        return true;
    }

    @FXML
    private void goToRegister(@NotNull ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException
    {
        System.out.println(getClass().getResource("/fxml/login.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
