package com.optiflow.models;

public class User
{
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role;

    public User()
    {}

    public User(String name, String email, String passwordHash, String role)
    {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getUserId()
    {
        return userId;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public String getRole()
    {
        return role;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public boolean isAdmin()
    {
        return role.equalsIgnoreCase("Admin");
    }

    public boolean isManager()
    {
        return role.equalsIgnoreCase("Manager");
    }

    public boolean isEmployee()
    {
        return role.equalsIgnoreCase("Employee");
    }

//    public boolean isValidEmail()
//    {}
}
