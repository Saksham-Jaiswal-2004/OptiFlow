package com.optiflow.controllers;

import com.optiflow.models.User;

import java.util.LinkedList;
import java.util.List;

public class UserController
{
    public boolean createUser(String name, String email, String password, String role)
    {
        return true;
    }

    public boolean updateUser(int userId, String name, String email)
    {
        return true;
    }

    public boolean updateUserRole(int userId, String role)
    {
        return true;
    }

    public boolean deleteUser(int userId)
    {
        return true;
    }

    public User getUserById(int userId)
    {
        User u = new User();
        return u;
    }

    public List<User> getAllUsers()
    {
        List<User> userList = new LinkedList<>();
        return userList;
    }
}
