package com.optiflow.models;

import java.sql.Date;
import java.util.List;

public class Tasks
{
    private int task_id;
    private int project_id;
    private int assigned_to;
    private String title;
    private String description;
    private String status;
    private String priority;
    private int estimated_hours;
    private int actual_hours;
    private Date start_date;
    private Date end_date;
    private List<String> skillsList;

    public Tasks()
    {
        System.out.println("Mai Tasks Hu!");
    }

    public Tasks(int project_id, int assigned_to, String title, String description, String status, String priority, int estimated_hours, Date start_date)
    {
        this.project_id = project_id;
        this.assigned_to = assigned_to;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.estimated_hours = estimated_hours;
        this.start_date = start_date;
    }

    public int getTask_id()
    {
        return task_id;
    }

    public int getProject_id()
    {
        return project_id;
    }

    public int getAssigned_to()
    {
        return assigned_to;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getStatus()
    {
        return status;
    }

    public String getPriority()
    {
        return priority;
    }

    public int getEstimated_hours()
    {
        return estimated_hours;
    }

    public int getActual_hours()
    {
        return actual_hours;
    }

    public Date getStart_date()
    {
        return start_date;
    }

    public Date getEnd_date()
    {
        return end_date;
    }

    public List<String> getSkillsList()
    {
        return skillsList;
    }

    public void setTask_id(int task_id)
    {
        this.task_id = task_id;
    }

    public void setProject_id(int project_id)
    {
        this.project_id = project_id;
    }

    public void setAssigned_to(int assigned_to)
    {
        this.assigned_to = assigned_to;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public void setEstimated_hours(int estimated_hours)
    {
        this.estimated_hours = estimated_hours;
    }

    public void setActual_hours(int actual_hours)
    {
        this.actual_hours = actual_hours;
    }

    public void setStart_date(Date start_date)
    {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date)
    {
        this.end_date = end_date;
    }

    public void setSkillsList(List<String> skillsList)
    {
        this.skillsList = skillsList;
    }
}
