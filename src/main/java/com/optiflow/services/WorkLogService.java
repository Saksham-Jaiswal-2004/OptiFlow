package com.optiflow.services;

import com.optiflow.dao.TaskDAO;
import com.optiflow.dao.WorkLogDAO;
import com.optiflow.models.WorkLog;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class WorkLogService
{
    private WorkLogDAO workLogDAO;
    private TaskDAO taskDAO;

    WorkLogService()
    {
        this.workLogDAO = new WorkLogDAO();
        this.taskDAO = new TaskDAO();
    }

    public boolean logWork(int empId, int taskId, int hours, String description) throws SQLException
    {
        if(empId <= 0 || taskId <= 0 || hours <= 0)
            return false;

        return workLogDAO.addWorkLog(empId, taskId, hours, description);
    }

    public int getTotalHoursWorked(int empId) throws SQLException
    {
        return workLogDAO.getTotalHoursByEmployee(empId);
    }

    public List<WorkLog> getEmployeeLogs(int empId) throws SQLException
    {
        return workLogDAO.getLogsByEmployee(empId);
    }

    public List<WorkLog> getEmployeeLogsByDate(int empId, Date date) throws SQLException
    {
        return workLogDAO.getLogsByEmployeeByDate(empId, date);
    }

    public int getTaskProgress(int taskId) throws SQLException
    {
        List<WorkLog> logs = workLogDAO.getLogsByTask(taskId);

        int total = 0;
        for(WorkLog log : logs)
        {
            total += log.getHoursWorked();
        }

        return total;
    }
}
