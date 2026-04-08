package com.optiflow.services;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.optiflow.dao.ProjectDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.dto.TaskDTO;
import com.optiflow.models.*;
import com.optiflow.utils.SessionManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ProjectService
{
    private ProjectDAO projectDAO;
    private TaskDAO taskDAO;
    private AIService aiService;
    private AuditLogService auditLogService;
    private ProjectSkillService projectSkillService;
    private EmployeeService employeeService;

    public ProjectService()
    {
        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
        this.aiService = new AIService();
        this.auditLogService = new AuditLogService();
        this.projectSkillService = new ProjectSkillService();
        this.employeeService = new EmployeeService();
    }

    public boolean createProject(Projects project) throws SQLException
    {
        return createProjectAndReturnId(project) > 0;
    }

    public int createProjectAndReturnId(Projects project) throws SQLException
    {
        if(project == null)
            return -1;

        if (project.getManager_id() > 0 && hasOpenProjectForManager(project.getManager_id(), null)) {
            return -1;
        }

        String normalizedStatus = normalizeProjectStatus(project.getStatus());
        project.setStatus(normalizedStatus);

        return projectDAO.createProjectAndReturnId(
                project.getName(),
                project.getDescription(),
                project.getStart_date(),
                project.getDeadline(),
                project.getManager_id(),
                normalizedStatus
        );
    }

    public List<Tasks> generateTasksForProjects(String project_title, String project_details)
    {
        return generateTasksForProjects(project_title, project_details, List.of());
    }

    public List<Tasks> generateTasksForProjects(String project_title, String project_details, List<String> availableSkills)
    {
        String response = aiService.generateTasks(project_title, project_details, availableSkills);
        System.out.println("Response: "+response);

        List<Tasks> tasksList = new LinkedList<>();
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

            System.out.println("Task: "+tasks);

            for(TaskDTO task: tasks)
            {
                Tasks t = new Tasks();
                t.setTitle(task.getTitle());
                t.setDescription(task.getDescription());
                t.setSkillsList(task.getSkills());
                t.setEstimated_hours(task.getEstimatedHours());
                t.setPriority(task.getPriority());

                tasksList.add(t);
            }

            auditLogService.logAction(SessionManager.getUser().getUserId(), "DIVIDE_PROJECT_TO_TASKS", "PROJECT", -1, SessionManager.getUser().getName()+" divided a project into its respective tasks");

            return tasksList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Projects getProjectById(int project_id) throws SQLException
    {
        if(project_id<=0)
            return null;

        return projectDAO.getProjectById(project_id);
    }

    public int getProjectByManager(int manager_id) throws SQLException
    {
        if(manager_id<=0)
            return -1;

        for(Projects p: projectDAO.getProjectsByManager(manager_id))
            return p.getProject_id();

        return -1;
    }

    public List<Projects> getAllProjects() throws SQLException
    {
        return projectDAO.getAllProjects();
    }

    public boolean updateProject(Projects project)
    {
        if(project == null)
            return false;

        try {
            if (project.getManager_id() > 0 && hasOpenProjectForManager(project.getManager_id(), project.getProject_id())) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        try
        {
            projectDAO.updateName(project.getProject_id(), project.getName());
            projectDAO.updateDescription(project.getProject_id(), project.getDescription());
            projectDAO.updateStartDate(project.getProject_id(), project.getStart_date());
            projectDAO.updateEndDate(project.getProject_id(), project.getEnd_date());
            projectDAO.updateDeadline(project.getProject_id(), project.getDeadline());
            projectDAO.updateStatus(project.getProject_id(), normalizeProjectStatus(project.getStatus()));

            auditLogService.logAction(SessionManager.getUser().getUserId(), "UPDATE_PROJECT", "PROJECT", project.getProject_id(), SessionManager.getUser().getName()+" updated the project "+project.getName());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeProjectStatus(String status)
    {
        if(status == null || status.isBlank())
            return "PLANNED";

        String normalized = status.trim().toUpperCase();
        if("IN PROGRESS".equals(normalized))
            normalized = "IN_PROGRESS";
        if("ON HOLD".equals(normalized))
            normalized = "ON_HOLD";

        return switch (normalized)
        {
            case "PLANNED", "IN_PROGRESS", "COMPLETED", "ON_HOLD" -> normalized;
            default -> "PLANNED";
        };
    }

    public boolean hasOpenProjectForManager(int managerId, Integer excludeProjectId) throws SQLException
    {
        return projectDAO.hasOpenProjectForManager(managerId, excludeProjectId);
    }

    public boolean isManagerAvailableForNewProject(int managerId) throws SQLException
    {
        return !hasOpenProjectForManager(managerId, null);
    }

    public Employee getBestManagerForProject(int projectId) throws SQLException
    {
        List<ProjectSkill> requiredSkills = projectSkillService.getSkillsForProject(projectId);

        List<Employee> managers = employeeService.getAllManagers();

        Employee bestManager = null;
        double bestScore = -1;

        for (Employee manager : managers)
        {
            double score = employeeService.calculateManagerScore(manager, requiredSkills);

            if (score > bestScore)
            {
                bestScore = score;
                bestManager = manager;
            }
        }

        return bestManager;
    }

    public boolean deleteProject(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "DELETE_PROJECT", "PROJECT", project_id, SessionManager.getUser().getName()+" deleted the project "+getProjectById(project_id).getName());

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

    public String exportProjectsToCSV() throws Exception
    {
        List<Projects> projects = projectDAO.getAllProjects();

        String fileName = "projects_" + LocalDate.now() + ".csv";

        FileWriter writer = new FileWriter(fileName);

        writer.append("ID,Name,Description,Start Date,End Date,Client-ID,Status\n");

        for (Projects p : projects) {
            writer.append(p.getProject_id() + ",")
                    .append(p.getName() + ",")
                    .append(p.getDescription() + ",")
                    .append(p.getStart_date() + ",")
                    .append(p.getEnd_date() + ",")
                    .append(p.getManager_id() + ",")
                    .append(p.getStatus() + "\n");
        }

        writer.flush();
        writer.close();

        auditLogService.logAction(SessionManager.getUser().getUserId(), "EXPORT_PROJECT", "PROJECT", SessionManager.getUser().getUserId(), SessionManager.getUser().getName()+" exported project details via CSV");

        return fileName;
    }

    public String exportProjectsToExcel() throws Exception
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Projects");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Description");
        header.createCell(3).setCellValue("Start Date");
        header.createCell(4).setCellValue("End Date");
        header.createCell(5).setCellValue("Client ID");
        header.createCell(6).setCellValue("Status");

        int rowNum = 1;

        for(Projects p : projectDAO.getAllProjects()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getProject_id());
            row.createCell(1).setCellValue(p.getName());
            row.createCell(2).setCellValue(p.getDescription());
            row.createCell(3).setCellValue(p.getStart_date());
            row.createCell(4).setCellValue(p.getEnd_date());
            row.createCell(5).setCellValue(p.getManager_id());
            row.createCell(6).setCellValue(p.getStatus());
        }

        FileOutputStream fileOut = new FileOutputStream("projects.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        auditLogService.logAction(SessionManager.getUser().getUserId(), "EXPORT_PROJECT", "PROJECT", SessionManager.getUser().getUserId(), SessionManager.getUser().getName()+" exported project details via Excel");

        return "";
    }

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        ProjectService projectService = new ProjectService();

        System.out.println("Enter Title: ");
        String title = sc.nextLine();
        System.out.println("Enter Description: ");
        String desc = sc.nextLine();

        projectService.generateTasksForProjects(title, desc);
    }
}
