package com.optiflow.services;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UserService
{
    private UserDAO userDAO;
    private AuthService auth;

    UserService()
    {
        this.userDAO = new UserDAO();
        this.auth = new AuthService();
    }

    public boolean createUser(User user) throws SQLException
    {
        if (user == null)
            return false;

        if (!auth.isValidEmail(user.getEmail()))
        {
            System.out.println("Invalid email format");
            return false;
        }

        if (userDAO.getUserByEmail(user.getEmail()) != null)
        {
            System.out.println("Email already exists");
            return false;
        }

        if (!auth.validatePassword(user.getPasswordHash()))
        {
            System.out.println("Weak password");
            return false;
        }

        String hashedPassword = auth.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        return userDAO.addUser(user.getName(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }

    public User getUserById(int user_id) throws SQLException
    {
        if(user_id <= 0)
            return null;

        return userDAO.getUserById(user_id);
    }

    public User getUserByEmail(String email) throws SQLException
    {
        if(!auth.isValidEmail(email))
            return null;

        return userDAO.getUserByEmail(email);
    }

    public List<User> getAllUsers() throws SQLException
    {
        return userDAO.getAllUsers();
    }

    public boolean updateUser(User user)
    {
        return true;
    }

    public boolean deleteUser(int user_id) throws SQLException
    {
        if(user_id <= 0)
            return false;

        return userDAO.deleteUser(user_id);
    }

    public List<User> getUsersByRole(String role) throws SQLException
    {
        if(role==null || role.isEmpty())
            return null;

        return userDAO.getUsersByRole(role);
    }
}
