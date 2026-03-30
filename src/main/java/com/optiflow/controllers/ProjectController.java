package com.optiflow.controllers;

public class ProjectController
{
    public boolean createProject(String name, String description, java.sql.Date startDate, java.sql.Date endDate)
    {
        return true;
    }

    public boolean updateProject(int projectId, String name, String description, java.sql.Date startDate, java.sql.Date endDate)
    {
        return true;
    }

    public boolean deleteProject(int projectId)
    {
        return true;
    }

    public Object getProjectById(int projectId)
    {
        return true;
    }

    public Object getAllProjects()
    {
        return true;
    }

    public boolean addEmployeeToProject(int projectId, int empId)
    {
        return true;
    }

    public boolean removeEmployeeFromProject(int projectId, int empId)
    {
        return true;
    }
}
