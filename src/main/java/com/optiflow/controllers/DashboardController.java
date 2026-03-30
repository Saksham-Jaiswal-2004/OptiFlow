package com.optiflow.controllers;

public class DashboardController
{
    public Object getAdminDashboardData()
    {
        return true;
    }

    public Object getManagerDashboardData(int managerId)
    {
        return true;
    }

    public Object getEmployeeDashboardData(int empId)
    {
        return true;
    }

    public int getTotalEmployees()
    {
        return -1;
    }

    public int getTotalProjects()
    {
        return -1;
    }

    public int getTotalTasks()
    {
        return -1;
    }

    public int getCompletedTasks()
    {
        return -1;
    }

    public int getPendingTasks()
    {
        return -1;
    }
}
