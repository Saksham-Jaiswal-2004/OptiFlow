package com.optiflow.controllers;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;
import com.optiflow.utils.AppContext;
import com.optiflow.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController
{
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    UserDAO userDAO = new UserDAO();

    @FXML
    public boolean handleLogin()
    {
        String email = emailField.getText();
        String password = passwordField.getText();

        try
        {

            User user = userDAO.getUserByEmail(email);

            if(user != null && user.getPasswordHash().equals(password))
            {
                messageLabel.setText("Login successful");

                SessionManager.setUser(user);
                AppContext.initSocket();

                return true;
            }
            else
            {
                messageLabel.setText("Invalid credentials");
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

    public boolean handleRegister(String name, String email, String password, String role)
    {
        return true;
    }

    public boolean handlePasswordChange(int userId, String oldPassword, String newPassword)
    {
        return true;
    }

    public boolean handlePasswordReset(String email)
    {
        return true;
    }
}
