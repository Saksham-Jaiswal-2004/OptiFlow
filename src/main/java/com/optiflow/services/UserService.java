package com.optiflow.services;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;
import java.sql.SQLException;
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

    public boolean updateName(int user_id, String name) throws SQLException
    {
        if(user_id<=0 || name.isEmpty())
            return false;

        if(userDAO.updateName(user_id, name)!=1)
            return false;

        return true;
    }

    public boolean updateEmail(int user_id, String email) throws SQLException
    {
        if(user_id<=0 || email.isEmpty() || !auth.isValidEmail(email))
            return false;

        if(userDAO.updateEmail(user_id, email)!=1)
            return false;

        return true;
    }

    public boolean updatePassword(int user_id, String password) throws SQLException
    {
        if(user_id<=0 || password.isEmpty() || !auth.validatePassword(password))
            return false;

        String hashed_password = auth.hashPassword(password);

        if(userDAO.updatePassword(user_id, hashed_password)!=1)
            return false;

        return true;
    }

    public boolean updateRole(int user_id, String role) throws SQLException
    {
        if(user_id<=0 || role.isEmpty())
            return false;

        if(userDAO.updateRole(user_id, role)!=1)
            return false;

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
