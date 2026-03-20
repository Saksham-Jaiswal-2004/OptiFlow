package com.optiflow.services;

import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import java.util.LinkedList;
import java.util.List;

public class ProjectService
{
    public boolean createProject(Projects project)
    {
        return true;
    }

    public Projects getProjectById(int projectId)
    {
        Projects p1 = new Projects();

        return p1;
    }

    public List<Projects> getAllProjects()
    {
        LinkedList<Projects> proList = new LinkedList<>();

        return proList;
    }

    public boolean updateProject(Projects project)
    {
        return true;
    }

    public boolean deleteProject(int projectId)
    {
        return true;
    }

    public double calculateProjectProgress(int projectId)
    {
        return 0.0;
    }

    public String getProjectStatus(int projectId)
    {
        return "";
    }

    public List<Tasks> getTasksByProject(int projectId)
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }

    public int getTotalProjectHours(int projectId)
    {
        return -1;
    }

    public int getCompletedProjectHours(int projectId)
    {
        return -1;
    }
}
