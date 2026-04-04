package com.optiflow.models;

public class Employee
{
    private int emp_id;
    private int user_id;
    private String name;
    private String designation;
    private String department;
    private int manager_id;
    private String status;
    private int weeklyCapacity;
    private int allocated_hours = 0;

    public Employee()
    {}

    public Employee(String name, String designation, String department, int manager_id, String status, int weeklyCapacity)
    {
        this.name = name;
        this.designation = designation;
        this.department = department;
        this.manager_id = manager_id;
        this.status = status;
        this.weeklyCapacity = weeklyCapacity;
    }

    public int getEmp_id()
    {
        return emp_id;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public String getName()
    {
        return name;
    }

    public String getDesignation()
    {
        return designation;
    }

    public String getDepartment()
    {
        return department;
    }

    public int getManager_id()
    {
        return manager_id;
    }

    public String getStatus()
    {
        return status;
    }

    public int getWeeklyCapacity()
    {
        return weeklyCapacity;
    }

    public int getAllocated_hours()
    {
        return allocated_hours;
    }

    public void setEmp_id(int emp_id)
    {
        this.emp_id = emp_id;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDesignation(String designation)
    {
        this.designation = designation;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public void setManager_id(int manager_id)
    {
        this.manager_id = manager_id;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setWeeklyCapacity(int weeklyCapacity)
    {
        this.weeklyCapacity = weeklyCapacity;
    }

    public void setAllocated_hours(int allocated_hours)
    {
        this.allocated_hours = allocated_hours;
    }

    public boolean isActive()
    {
        return status.equalsIgnoreCase("Active");
    }

    public boolean isManager()
    {
        return designation.equalsIgnoreCase("Manager");
    }

    public boolean hasCapacity()
    {
        return allocated_hours < weeklyCapacity;
    }

    public int getRemainingCapacity()
    {
        return weeklyCapacity-allocated_hours;
    }
}
