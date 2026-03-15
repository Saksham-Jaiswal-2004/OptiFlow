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
}
