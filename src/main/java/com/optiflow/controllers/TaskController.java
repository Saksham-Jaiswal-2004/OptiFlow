package com.optiflow.controllers;

public class TaskController
{
    public boolean createTask(int projectId, int assignedTo, String title, String description, String status, String priority, int estimatedHours, java.sql.Date startDate, java.sql.Date endDate)
    {
        return true;
    }

    public boolean updateTask(int taskId, String title, String description, String status, String priority, java.sql.Date startDate, java.sql.Date endDate)
    {
        return true;
    }

    public boolean deleteTask(int taskId)
    {
        return true;
    }

    public boolean assignTask(int taskId, int empId)
    {
        return true;
    }

    public boolean autoAssignTask(int taskId)
    {
        return true;
    }

    public boolean updateTaskStatus(int taskId, String status)
    {
        return true;
    }

    public Object getTaskById(int taskId)
    {
        return true;
    }

    public Object getTasksByEmployee(int empId)
    {
        return true;
    }

    public Object getTasksByStatus(String status)
    {
        return true;
    }

    public Object getOverdueTasks()
    {
        return true;
    }

    public Object getTasksDueSoon()
    {
        return true;
    }
}
