package com.optiflow.models;

public class User
{
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role;

    public User()
    {
        System.out.println("Mai User Hu!");
    }

    public User(String name, String email, String passwordHash, String role)
    {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getPasswordHash()
    {
        System.out.println("Password Hash dunga mai!");

        return "Password Hash";
    }
}
