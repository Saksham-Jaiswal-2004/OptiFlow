package com.optiflow.controllers;

public class AuthController
{
    public boolean handleLogin(String email, String password)
    {
        return true;
    }

    public void handleLogout()
    {}

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
