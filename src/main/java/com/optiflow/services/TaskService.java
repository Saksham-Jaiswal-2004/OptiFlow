package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Tasks;

import java.sql.SQLException;
import java.util.List;

public class TaskService
{
    private TaskDAO taskDAO;
    private EmployeeDAO employeeDAO;

    TaskService()
    {
        this.taskDAO = new TaskDAO();
        this.employeeDAO = new EmployeeDAO();
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

    public boolean updateTask(Tasks task)
    {
        return true;
    }

    public boolean deleteTask(int task_id) throws SQLException
    {
        if(task_id <= 0)
            return false;

        return taskDAO.deleteTask(task_id) == 1;
    }

    public boolean assignTask(int task_id, int emp_id) throws SQLException
    {
        if(task_id <=0 || emp_id <= 0)
            return false;

        return taskDAO.assignTask(task_id, emp_id) == 1;
    }

    public boolean autoAssignTask(Tasks task)
    {
        return true;
    }

    public boolean updateTaskStatus(int task_id, String status) throws SQLException
    {
        if(task_id <= 0)
            return false;

        return taskDAO.updateStatus(task_id,status) == 1;
    }

    public List<Tasks> getTasksByStatus(String status) throws SQLException
    {
        return taskDAO.getTasksByStatus(status);
    }

    public List<Tasks> getTasksByEmployee(int emp_id) throws SQLException
    {
        if(emp_id <= 0)
            return null;

        return taskDAO.getTasksByEmployee(emp_id);
    }

    public int calculateTaskHours(int task_id)
    {
        return -1;
    }

    public boolean canAssignTask(int emp_id, int taskHours)
    {
        if(emp_id <= 0)
            return false;

        if(employeeDAO.getWeeklyCapacity(emp_id) - employeeDAO.getAllocatedHours(emp_id) > taskHours)
            return true;

        return false;
    }

    public List<Tasks> getOverdueTasks() throws SQLException
    {
        return taskDAO.getDelayedTasks();
    }

    public List<Tasks> getTasksDueSoon() throws SQLException
    {
        return taskDAO.getTasksDueSoon();
    }
}
