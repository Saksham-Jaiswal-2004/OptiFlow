package com.optiflow.models;

import java.util.Date;

public class Projects
{
    private int project_id;
    private String name;
    private String description;
    private Date start_date;
    private Date end_date;
    private int client_id;
    private String status;

    Projects()
    {
        System.out.println("Mai Projects Hu!");
    }

    Projects(String name, String description, Date start_date, int client_id)
    {
        this.name = name;
        this.description = description;
        this.start_date = start_date;
        this.client_id = client_id;
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

    public int getClient_id()
    {
        return client_id;
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

    public void setClient_id(int client_id)
    {
        this.client_id = client_id;
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
