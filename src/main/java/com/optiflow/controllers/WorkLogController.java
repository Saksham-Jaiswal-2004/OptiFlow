package com.optiflow.controllers;

public class WorkLogController
{
    public boolean logWork(int taskId, int hours, String description)
    {
        return true;
    }

    public boolean updateWorkLog(int logId, int hours, String description)
    {
        return true;
    }

    public boolean deleteWorkLog(int logId)
    {
        return true;
    }

    public Object getLogsByEmployee(int empId)
    {
        return true;
    }

    public Object getLogsByTask(int taskId)
    {
        return true;
    }

    public Object getLogsByDateRange(java.sql.Date startDate, java.sql.Date endDate)
    {
        return true;
    }

    public int getTotalHoursByEmployee(int empId)
    {
        return -1;
    }
}
