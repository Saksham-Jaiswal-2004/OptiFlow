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
}
