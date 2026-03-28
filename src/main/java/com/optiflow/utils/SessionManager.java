package com.optiflow.utils;

import com.optiflow.models.User;

public class SessionManager
{
    private static User currentUser;

    public static void setUser(User user)
    {
        currentUser = user;
    }

    public static User getUser()
    {
        return currentUser;
    }
}
