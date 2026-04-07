package com.optiflow.models;

import java.sql.Date;

public class Projects
{
    private int project_id;
    private String name;
    private String description;
    private Date start_date;
    private Date end_date;
    private Date deadline;
    private int manager_id;
    private String status;

    public Projects()
    {}

    public Projects(String name, String description, Date start_date, Date deadline, int manager_id, String status)
    {
        this.name = name;
        this.description = description;
        this.start_date = start_date;
        this.deadline = deadline;
        this.manager_id = manager_id;
        this.status = status;
    }

    public int getProject_id()
    {
        return project_id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getStart_date()
    {
        return start_date;
    }

    public Date getEnd_date()
    {
        return end_date;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public int getManager_id()
    {
        return manager_id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setProject_id(int project_id)
    {
        this.project_id = project_id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setStart_date(Date start_date)
    {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date)
    {
        this.end_date = end_date;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public void setManager_id(int client_id)
    {
        this.manager_id = client_id;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public boolean isActive()
    {
        return status.equalsIgnoreCase("Active");
    }

    public boolean isCompleted()
    {
        return status.equalsIgnoreCase("Completed");
    }

    public boolean isDelayed()
    {
        return status.equalsIgnoreCase("Delayed");
    }
}
