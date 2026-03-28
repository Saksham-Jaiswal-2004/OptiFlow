package com.optiflow.services;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.optiflow.dao.ProjectDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.dto.TaskDTO;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ProjectService
{
    ProjectDAO projectDAO;
    TaskDAO taskDAO;
    AIService aiService;

    ProjectService()
    {
        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
        this.aiService = new AIService();
    }

    public boolean createProject(Projects project) throws SQLException
    {
        if(project == null)
            return false;

        return projectDAO.createProject(project.getName(), project.getDescription(), project.getStart_date(), project.getEnd_date(), project.getClient_id(), project.getStatus());
    }

    public void generateTasksForProjects(String project_title, String project_details)
    {
        String response = aiService.generateTasks(project_title, project_details);
        System.out.println("Response: "+response);

        List<TaskDTO> tasks = null;
        try
        {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            content = content.replace("```json", "")
                    .replace("```", "")
                    .trim();

            tasks = mapper.readValue(
                    content,
                    new TypeReference<List<TaskDTO>>() {}
            );

            System.out.println("Taske: "+tasks);

            for(TaskDTO task: tasks)
            {
                System.out.println(task.getTitle());
                System.out.println(task.getDescription());
                System.out.println(task.getEstimatedHours());
                System.out.println(task.getPriority());
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        ProjectService projectService = new ProjectService();

        System.out.print("Enter project title: ");
        String title = sc.nextLine();
        System.out.print("Enter project details: ");
        String description = sc.nextLine();

        projectService.generateTasksForProjects(title, description);
    }
}
