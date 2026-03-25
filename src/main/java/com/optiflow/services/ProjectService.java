package com.optiflow.services;

import com.optiflow.dao.ProjectDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ProjectService
{
    ProjectDAO projectDAO;
    TaskDAO taskDAO;

    ProjectService()
    {
        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
    }

    public boolean createProject(Projects project) throws SQLException
    {
        if(project == null)
            return false;

        return projectDAO.createProject(project.getName(), project.getDescription(), project.getStart_date(), project.getEnd_date(), project.getClient_id(), project.getStatus());
    }

    public Projects getProjectById(int project_id) throws SQLException
    {
        if(project_id<=0)
            return null;

        return projectDAO.getProjectById(project_id);
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

    public boolean deleteProject(int project_id)
    {
        return true;
    }

    public double calculateProjectProgress(int project_id)
    {
        return 0.0;
    }

    public String getProjectStatus(int project_id)
    {
        return "";
    }

    public List<Tasks> getTasksByProject(int project_id)
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }

    public int getTotalProjectHours(int project_id)
    {
        return -1;
    }

    public int getCompletedProjectHours(int project_id)
    {
        return -1;
    }
}
