package com.optiflow.controllers;

import com.optiflow.models.Projects;
import com.optiflow.services.ProjectService;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

public class ProjectController
{
    private final ProjectService projectService;

    public ProjectController() {
        this.projectService = new ProjectService();
    }

    public boolean createProject(String name, String description, java.sql.Date startDate, java.sql.Date endDate)
    {
        try {
            Projects project = new Projects();
            project.setName(name);
            project.setDescription(description);
            project.setStart_date(startDate);
            project.setEnd_date(endDate);
            project.setDeadline(endDate);
            project.setStatus("Active");
            project.setManager_id(1);
            return projectService.createProject(project);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateProject(int projectId, String name, String description, java.sql.Date startDate, java.sql.Date endDate)
    {
        try {
            Projects project = projectService.getProjectById(projectId);
            if (project == null) {
                return false;
            }

            project.setName(name);
            project.setDescription(description);
            project.setStart_date(startDate);
            project.setEnd_date(endDate);
            project.setDeadline(endDate);
            return projectService.updateProject(project);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteProject(int projectId)
    {
        try {
            return projectService.deleteProject(projectId);
        } catch (Exception e) {
            return false;
        }
    }

    public Projects getProjectById(int projectId)
    {
        try {
            return projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Projects> getAllProjects()
    {
        try {
            List<Projects> projects = projectService.getAllProjects();
            return projects == null ? Collections.emptyList() : projects;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean addEmployeeToProject(int projectId, int empId)
    {
        // No direct service method exists for this operation in current service layer.
        return false;
    }

    public boolean removeEmployeeFromProject(int projectId, int empId)
    {
        // No direct service method exists for this operation in current service layer.
        return false;
    }
}
