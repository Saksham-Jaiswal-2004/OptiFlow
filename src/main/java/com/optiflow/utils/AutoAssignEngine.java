package com.optiflow.utils;

import com.optiflow.dao.*;
import com.optiflow.models.*;

import java.sql.SQLException;
import java.util.*;

public class AutoAssignEngine
{

    private EmployeeDAO employeeDAO;
    private TaskSkillDAO taskSkillDAO;
    private EmployeeSkillDAO employeeSkillDAO;
    private TaskDAO taskDAO;

    public AutoAssignEngine()
    {
        this.employeeDAO = new EmployeeDAO();
        this.taskSkillDAO = new TaskSkillDAO();
        this.employeeSkillDAO = new EmployeeSkillDAO();
        this.taskDAO = new TaskDAO();
    }

    public Employee getBestEmployeeForTask(int taskId, int managerId) throws SQLException
    {
        List<TaskSkill> requiredSkills = taskSkillDAO.getSkillsByTask(taskId);

        if(requiredSkills == null || requiredSkills.isEmpty())
            return null;

        List<Employee> employees = employeeDAO.getEmployeesByManager(managerId);

        Employee bestEmployee = null;
        double bestScore = -1;

        for(Employee emp : employees)
        {
            List<Skills> empSkills = employeeSkillDAO.getSkillsByEmployee(emp.getEmp_id());

            List<Integer> empSkillIds = new ArrayList<>();
            for (Skills skill : empSkills)
            {
                empSkillIds.add(skill.getSkill_id());
            }

            int matchCount = 0;

            for(TaskSkill ts : requiredSkills)
            {
                if(empSkillIds.contains(ts.getSkillId()))
                {
                    matchCount++;
                }
            }

            double skillScore = (double) matchCount / requiredSkills.size();

            int allocated = employeeDAO.getAllocatedHours(emp.getEmp_id());
            int capacity = employeeDAO.getWeeklyCapacity(emp.getEmp_id());

            double workloadScore = 1.0 - ((double) allocated / capacity);

            if(workloadScore < 0)
                workloadScore = 0;

            if(skillScore < 0.5)
                continue;

            if(allocated >= capacity)
                continue;

            double finalScore = (0.7 * skillScore) + (0.3 * workloadScore);

            if(finalScore > bestScore)
            {
                bestScore = finalScore;
                bestEmployee = emp;
            }
        }

        return bestEmployee;
    }
}