package com.optiflow.controllers;

import com.optiflow.models.User;
import com.optiflow.services.UserService;

import java.util.Collections;
import java.util.List;

public class UserController
{
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public boolean createUser(String name, String email, String password, String role)
    {
        try {
            User user = new User(name, email, password, role);
            return userService.createUser(user);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateUser(int userId, String name, String email)
    {
        try {
            boolean nameUpdated = userService.updateName(userId, name);
            boolean emailUpdated = userService.updateEmail(userId, email);
            return nameUpdated && emailUpdated;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateUserRole(int userId, String role)
    {
        try {
            return userService.updateRole(userId, role);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteUser(int userId)
    {
        try {
            return userService.deleteUser(userId);
        } catch (Exception e) {
            return false;
        }
    }

    public User getUserById(int userId)
    {
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> getAllUsers()
    {
        try {
            List<User> users = userService.getAllUsers();
            return users == null ? Collections.emptyList() : users;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
