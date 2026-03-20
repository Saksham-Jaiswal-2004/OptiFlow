package com.optiflow.services;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;

import java.sql.SQLException;
import java.util.Scanner;

public class AuthService
{
    private UserDAO userDAO;

    public AuthService()
    {
        this.userDAO = new UserDAO();
    }

    public User login(String email, String password) throws SQLException
    {
        User user = userDAO.getUserByEmail(email);

        if(user == null)
        {
            System.out.println("User with this email does not exist!");
            return null;
        }

        if(password.equals(user.getPasswordHash()))
        {
            System.out.println("Login Successful!");
            return user;
        }

        System.out.println("Invalid Login Credentials!");
        return user;
    }

    public boolean register(User user)
    {
        return true;
    }

    public boolean logout(int userId)
    {
        return true;
    }

    public boolean validatePassword(String password)
    {
        return true;
    }

    public boolean isEmailExists(String email)
    {
        return true;
    }

    public String hashPassword(String password)
    {
        return "";
    }

    public boolean verifyPassword(String input, String hash)
    {
        return true;
    }
}
