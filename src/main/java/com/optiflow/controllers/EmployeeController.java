package com.optiflow.controllers;

public class EmployeeController
{
    public boolean createEmployee(int userId, String name, String skill, String designation, String department, int managerId, String status, int weeklyCapacity)
    {
        return true;
    }

    public boolean updateEmployee(int empId, String name, String skill, String designation, String department, int managerId, String status, int weeklyCapacity)
    {
        return true;
    }

    public boolean deleteEmployee(int empId)
    {
        return true;
    }

    public Object getEmployeeById(int empId)
    {
        return true;
    }

    public Object getAllEmployees()
    {
        return true;
    }

    public Object getEmployeesByDepartment(String department)
    {
        return true;
    }

    public Object getEmployeesByManager(int managerId)
    {
        return true;
    }

    public boolean assignManager(int empId, int managerId)
    {
        return true;
    }
}
