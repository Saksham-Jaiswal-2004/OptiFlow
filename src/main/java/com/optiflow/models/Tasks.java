package com.optiflow.models;

import java.util.Date;

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

    Tasks()
    {
        System.out.println("Mai Tasks Hu!");
    }
}
