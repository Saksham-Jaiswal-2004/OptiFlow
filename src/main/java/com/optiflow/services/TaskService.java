package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.dao.TaskSkillDAO;
import com.optiflow.dao.UserDAO;
import com.optiflow.models.*;
import com.optiflow.networking.Message;
import com.optiflow.networking.MessageType;
import com.optiflow.utils.AppContext;
import com.optiflow.utils.AutoAssignEngine;
import com.optiflow.utils.SessionManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TaskService
{
    private TaskDAO taskDAO;
    private EmployeeDAO employeeDAO;
    private EmployeeSkillService employeeSkillService;
    private WorkloadService workloadService;
    private TaskSkillService taskSkillService;
    private AuditLogService auditLogService;

     public TaskService()
    {
        this.taskDAO = new TaskDAO();
        this.employeeDAO = new EmployeeDAO();
        this.employeeSkillService = new EmployeeSkillService();
        this.workloadService = new WorkloadService();
        this.taskSkillService = new TaskSkillService();
        this.auditLogService = new AuditLogService();
    }

    public boolean createTask(Tasks task) throws SQLException
    {
        if(task == null)
            return false;

        return taskDAO.createTask(task.getProject_id(), task.getAssigned_to(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getEstimated_hours(), task.getStart_date(), task.getEnd_date());
    }

    public Tasks getTaskById(int task_id) throws SQLException
    {
        if(task_id <= 0)
            return null;

        return taskDAO.getTaskById(task_id);
    }

    public List<Tasks> getTaskByEmp(int emp_id) throws SQLException
    {
        if(emp_id <= 0)
            return null;

        return taskDAO.getTasksByEmployee(emp_id);
    }

    public List<Tasks> getTaskByProject(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return null;

        return taskDAO.getTasksByProject(project_id);
    }

    public boolean updateTask(Tasks task)
    {
        if(task == null)
            return false;

        try
        {
            taskDAO.updateTitle(task.getTask_id(), task.getTitle());
            taskDAO.updateDescription(task.getTask_id(), task.getDescription());
            taskDAO.updateStatus(task.getTask_id(), task.getStatus());
            taskDAO.updatePriority(task.getTask_id(), task.getPriority());
            taskDAO.updateEstimatedHours(task.getTask_id(), task.getEstimated_hours());
            taskDAO.updateActualHours(task.getTask_id(), task.getActual_hours());
            taskDAO.updateStartDate(task.getTask_id(), task.getStart_date());
            taskDAO.updateEndDate(task.getTask_id(), task.getEnd_date());

            auditLogService.logAction(SessionManager.getUser().getUserId(), "UPDATE_TASK", "TASK", task.getTask_id(), SessionManager.getUser().getName()+" updated a task");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteTask(int task_id) throws SQLException
    {
        if(task_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "DELETE_TASK", "TASK", task_id, SessionManager.getUser().getName()+" deleted a task");

        return taskDAO.deleteTask(task_id) == 1;
    }

    public boolean assignTask(int task_id, int emp_id) throws Exception
    {
        if(task_id <=0 || emp_id <= 0)
            return false;

        if(taskDAO.assignTask(task_id, emp_id) == 1)
        {
            auditLogService.logAction(SessionManager.getUser().getUserId(), "ASSIGN_TASK", "TASK", task_id, SessionManager.getUser().getName()+" assigned a task to "+employeeDAO.getEmployeeById(emp_id).getName());

            AppContext.socketClient.sendMessage(
                new Message(MessageType.TASK_UPDATE, "New Task Assigned", SessionManager.getUser().getUserId(), emp_id, "EMPLOYEE")
            );

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean autoAssignTask(Tasks task) throws Exception
    {
        if(task == null)
            return false;

        AutoAssignEngine engine = new AutoAssignEngine();

        Employee bestEmp = engine.getBestEmployeeForTask(task.getTask_id());

        if(bestEmp == null)
            return false;

        if(taskDAO.assignTask(task.getTask_id(), bestEmp.getEmp_id()) == 1)
        {
            auditLogService.logAction(SessionManager.getUser().getUserId(), "ASSIGN_TASK", "TASK", task.getTask_id(), "AI assigned a task to "+bestEmp.getName());

            AppContext.socketClient.sendMessage(
                    new Message(MessageType.COMMENT, "New Task Assigned", SessionManager.getUser().getUserId(), bestEmp.getEmp_id(), "EMPLOYEE")
            );

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean updateTaskStatus(int task_id, String status) throws SQLException
    {
        if(task_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "UPDATE_TASK_STATUS", "TASK", task_id, SessionManager.getUser().getName()+" updated task status to "+status);

        return taskDAO.updateStatus(task_id,status) == 1;
    }

    public List<Tasks> getOverdueTasks() throws SQLException
    {
        return taskDAO.getDelayedTasks();
    }

    public List<Tasks> getTasksDueSoon() throws SQLException
    {
        return taskDAO.getTasksDueSoon();
    }

    public String exportTasksToCSV(int project_id, String filePath) throws Exception
    {
        List<Tasks> tasks = taskDAO.getTasksByProject(project_id);

        FileWriter writer = new FileWriter(filePath);

        writer.append("ID,Project ID,Title,Description,Status,Priority,Assigned To,Estimated Hours,Actual Hours,Start Date,End Date\n");

        for (Tasks t : tasks) {
            writer.append(t.getTask_id() + ",")
                    .append(t.getProject_id() + ",")
                    .append("\"" + t.getTitle() + "\",")
                    .append("\"" + t.getDescription() + "\",")
                    .append(t.getStatus() + ",")
                    .append(t.getPriority() + ",")
                    .append(t.getAssigned_to() + ",")
                    .append(t.getEstimated_hours() + ",")
                    .append(t.getActual_hours() + ",")
                    .append(t.getStart_date() + ",")
                    .append(t.getEnd_date() + "\n");
        }

        writer.flush();
        writer.close();

        auditLogService.logAction(
                SessionManager.getUser().getUserId(),
                "EXPORT_TASK",
                "TASK",
                -1,
                SessionManager.getUser().getName() + " exported task details via CSV"
        );

        return filePath;
    }

    public String exportTasksToExcel() throws Exception
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tasks");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Project ID");
        header.createCell(2).setCellValue("Title");
        header.createCell(3).setCellValue("Description");
        header.createCell(4).setCellValue("Status");
        header.createCell(5).setCellValue("Priority");
        header.createCell(6).setCellValue("Assigned To");
        header.createCell(7).setCellValue("Estimated Hours");
        header.createCell(8).setCellValue("Actual Hours");
        header.createCell(9).setCellValue("Start Date");
        header.createCell(10).setCellValue("End Date");

        int rowNum = 1;

        for(Tasks t : taskDAO.getAllTasks())
        {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getTask_id());
            row.createCell(1).setCellValue(t.getProject_id());
            row.createCell(2).setCellValue(t.getTitle());
            row.createCell(3).setCellValue(t.getDescription());
            row.createCell(4).setCellValue(t.getStatus());
            row.createCell(5).setCellValue(t.getPriority());
            row.createCell(6).setCellValue(t.getAssigned_to());
            row.createCell(7).setCellValue(t.getEstimated_hours());
            row.createCell(8).setCellValue(t.getActual_hours());
            row.createCell(9).setCellValue(t.getStart_date());
            row.createCell(10).setCellValue(t.getEnd_date());
        }

        FileOutputStream fileOut = new FileOutputStream("tasks.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        auditLogService.logAction(SessionManager.getUser().getUserId(), "EXPORT_TASK", "TASK", -1, SessionManager.getUser().getName()+" exported task details via Excel");

        return "";
    }
}
