package com.optiflow.services;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.regex.Pattern;

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
            System.out.println("User with this email does not exist, Please Register!");
            return null;
        }

        if(verifyPassword(password, user.getPasswordHash()))
        {
            System.out.println("Login Successful!");
            return user;
        }

        System.out.println("Invalid Login Credentials!");
        return null;
    }

    public boolean register(User user) throws SQLException {
        if (user == null)
            return false;

        if (!isValidEmail(user.getEmail()))
        {
            System.out.println("Invalid email format");
            return false;
        }

        if (userDAO.getUserByEmail(user.getEmail()) != null)
        {
            System.out.println("Email already exists");
            return false;
        }

        if (!validatePassword(user.getPasswordHash()))
        {
            System.out.println("Weak password");
            return false;
        }

        String hashedPassword = hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        return userDAO.addUser(user.getName(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }

    public boolean logout(int userId)
    {
        return true;
    }

    public boolean validatePassword(String password)
    {
        if (password.length() < 8)
            return false;

        boolean hasUpper = false, hasLower = false, hasDigit = false;

        for (char c : password.toCharArray())
        {
            if (Character.isUpperCase(c))
                hasUpper = true;
            if (Character.isLowerCase(c))
                hasLower = true;
            if (Character.isDigit(c))
                hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }

    public boolean isValidEmail(String email)
    {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(regex, email);
    }

    public String hashPassword(String password)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes)
            {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) hexString.append('0');

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean verifyPassword(String input, String storedHash)
    {
        String hashedInput = hashPassword(input);
        return hashedInput.equals(storedHash);
    }
}
