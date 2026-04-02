package com.optiflow.models;

import java.sql.Date;

public class WorkLog
{

    private int logId;
    private int employeeId;
    private int taskId;
    private Date workDate;
    private int hoursWorked;
    private String description;

    public WorkLog()
    {}

    public WorkLog(int employeeId, int taskId, Date workDate, int hoursWorked, String description)
    {
        this.employeeId = employeeId;
        this.taskId = taskId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.description = description;
    }

    public int getLogId()
    {
        return logId;
    }

    public int getEmployeeId()
    {
        return employeeId;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public Date getWorkDate()
    {
        return workDate;
    }

    public int getHoursWorked()
    {
        return hoursWorked;
    }

    public String getDescription()
    {
        return description;
    }

    public void setEmployeeId(int employeeId)
    {
        this.employeeId = employeeId;
    }

    public void setLogId(int logId)
    {
        this.logId = logId;
    }

    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    public void setWorkDate(Date workDate)
    {
        this.workDate = workDate;
    }

    public void setHoursWorked(int hoursWorked)
    {
        this.hoursWorked = hoursWorked;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
