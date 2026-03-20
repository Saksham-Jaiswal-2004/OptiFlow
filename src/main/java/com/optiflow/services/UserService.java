package com.optiflow.services;

import com.optiflow.models.User;
import java.util.LinkedList;
import java.util.List;

public class UserService
{
    public boolean createUser(User user)
    {
        return true;
    }

    public User getUserById(int userId)
    {
        User u1 = new User();

        return u1;
    }

    public User getUserByEmail(String email)
    {
        User u1 = new User();

        return u1;
    }

    public List<User> getAllUsers()
    {
        LinkedList<User> userList = new LinkedList<>();

        return userList;
    }

    public boolean updateUser(User user)
    {
        return true;
    }

    public boolean deleteUser(int userId)
    {
        return true;
    }

    public List<User> getUsersByRole(String role)
    {
        LinkedList<User> userList = new LinkedList<>();

        return userList;
    }
}
