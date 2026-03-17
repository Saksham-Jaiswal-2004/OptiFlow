package com.optiflow.models;

public class EmployeeSkill
{
    private int emp_id;
    private int skill_id;
    private int proficiency;

    EmployeeSkill()
    {
        System.out.println("Mai Employee Skills Connector Hu!");
    }

    EmployeeSkill(int emp_id, int skill_id, int proficiency)
    {
        this.emp_id = emp_id;
        this.skill_id = skill_id;
        this.proficiency = proficiency;
    }

    public int getEmp_id()
    {
        return emp_id;
    }

    public int getSkill_id()
    {
        return skill_id;
    }

    public int getProficiency()
    {
        return proficiency;
    }

    public void setEmp_id(int emp_id)
    {
        this.emp_id = emp_id;
    }

    public void setSkill_id(int skill_id)
    {
        this.skill_id = skill_id;
    }

    public void setProficiency(int proficiency)
    {
        this.proficiency = proficiency;
    }
}
