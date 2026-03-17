package com.optiflow.models;

import java.util.Date;

public class Workload
{
    private int workload_id;
    private int employee_id;
    private int task_id;
    private int hours_allocated;
    private int hours_logged;
    private Date date;

    Workload()
    {
        System.out.println("Mai WorkLoad Hu!");
    }

    Workload(int employee_id, int task_id, int hours_allocated, int hours_logged, Date date)
    {
        this.employee_id = employee_id;
        this.task_id = task_id;
        this.hours_allocated = hours_allocated;
        this.hours_logged = hours_logged;
        this.date = date;
    }

    public int getWorkload_id()
    {
        return workload_id;
    }

    public int getEmployee_id()
    {
        return employee_id;
    }

    public int getTask_id()
    {
        return task_id;
    }

    public int getHours_allocated()
    {
        return hours_allocated;
    }

    public int getHours_logged()
    {
        return hours_logged;
    }

    public Date getDate()
    {
        return date;
    }

    public void setEmployee_id(int employee_id)
    {
        this.employee_id = employee_id;
    }

    public void setTask_id(int task_id)
    {
        this.task_id = task_id;
    }

    public void setHours_allocated(int hours_allocated)
    {
        this.hours_allocated = hours_allocated;
    }

    public void setHours_logged(int hours_logged)
    {
        this.hours_logged = hours_logged;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
