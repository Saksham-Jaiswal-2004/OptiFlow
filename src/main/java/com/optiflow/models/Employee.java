package com.optiflow.models;

public class Employee
{
    private int emp_id;
    private int user_id;
    private String name;
    private String skill;
    private String designation;
    private String department;
    private int manager_id;
    private String status;
    private int weeklyCapacity;

    public Employee()
    {
        System.out.println("Mai Employee Hu!");
    }

    public Employee(String name, String skill, String designation, String department, int manager_id, String status, int weeklyCapacity)
    {
        this.name = name;
        this.skill = skill;
        this.designation = designation;
        this.department = department;
        this.manager_id = manager_id;
        this.status = status;
        this.weeklyCapacity = weeklyCapacity;
    }
}
