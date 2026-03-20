package com.optiflow.services;

import com.optiflow.models.Employee;
import com.optiflow.models.Skills;

import java.util.LinkedList;
import java.util.List;

public class SkillService
{
    public boolean addSkill(Skills skill)
    {
        return true;
    }

    public List<Skills> getAllSkills()
    {
        LinkedList<Skills> skillList = new LinkedList<>();

        return skillList;
    }

    public List<Employee> getEmployeesBySkill(String skill)
    {
        LinkedList<Employee> empList = new LinkedList<>();

        return empList;
    }

    public boolean deleteSkill(int skillId)
    {
        return true;
    }

}
