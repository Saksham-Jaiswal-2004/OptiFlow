package com.optiflow.utils;

import com.optiflow.models.User;

public class AuthorizationUtil
{
    public static boolean isAdmin(User user)
    {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public static boolean isManager(User user)
    {
        return user != null && "MANAGER".equalsIgnoreCase(user.getRole());
    }

    public static boolean isEmployee(User user)
    {
        return user != null && "EMPLOYEE".equalsIgnoreCase(user.getRole());
    }
}
