package com.optiflow.services;

import com.optiflow.models.User;

public class AuthService
{
    public User login(String email, String password)
    {
        User u1 = new User();

        return u1;
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
