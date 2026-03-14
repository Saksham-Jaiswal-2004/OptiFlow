package com.optiflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;

public class LoginController
{
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin()
    {

        String email = emailField.getText();
        String password = passwordField.getText();

        try {

            User user = userDAO.getUserByEmail(email);

            if(user != null && user.getPasswordHash().equals(password))
            {
                messageLabel.setText("Login successful");
            }
            else
            {
                messageLabel.setText("Invalid credentials");
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
