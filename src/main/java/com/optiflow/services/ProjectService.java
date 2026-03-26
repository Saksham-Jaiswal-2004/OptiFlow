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

    public List<Projects> getAllProjects() throws SQLException
    {
        return projectDAO.getAllProjects();
    }

    public boolean updateProject(Projects project)
    {
        if(project == null)
            return false;

        try
        {
            projectDAO.updateName(project.getProject_id(), project.getName());
            projectDAO.updateDescription(project.getProject_id(), project.getDescription());
            projectDAO.updateStartDate(project.getProject_id(), project.getStart_date());
            projectDAO.updateEndDate(project.getProject_id(), project.getEnd_date());
            projectDAO.updateStatus(project.getProject_id(), project.getStatus());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteProject(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return false;

        return projectDAO.deleteProject(project_id) == 1;
    }

    public double calculateProjectProgress(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return -1.0;

        return (getCompletedProjectHours(project_id)/getTotalProjectHours(project_id))*100;
    }

    public String getProjectStatus(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return "";

        return projectDAO.getProjectStatus(project_id);
    }

    public List<Tasks> getTasksByProject(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return null;

        return taskDAO.getTasksByProject(project_id);
    }

    public int getTotalProjectHours(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return -1;

        List<Tasks> taskList = getTasksByProject(project_id);
        int total = 0;

        for(Tasks task: taskList)
        {
            total += task.getEstimated_hours();
        }

        return total;
    }

    public int getCompletedProjectHours(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return -1;

        List<Tasks> taskList = getTasksByProject(project_id);
        int total = 0;

        for(Tasks task: taskList)
        {
            total += task.getActual_hours();
        }

        return total;
    }
}
