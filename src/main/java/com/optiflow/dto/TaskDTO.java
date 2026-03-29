package com.optiflow.dto;

import java.util.List;

public class TaskDTO
{
    public String title;
    public String description;
    public List<String> skills;
    public int estimated_hours;
    public String priority;

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public List<String> getSkills()
    {
        return skills;
    }

    public int getEstimatedHours()
    {
        return estimated_hours;
    }

    public String getPriority()
    {
        return priority;
    }
}
