package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.EmployeeSkillDAO;
import com.optiflow.dao.SkillsDAO;
import com.optiflow.models.Employee;
import com.optiflow.models.Skills;
import com.optiflow.utils.SessionManager;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EmployeeSkillService
{
    private EmployeeSkillDAO employeeSkillDAO;
    private EmployeeDAO employeeDAO;
    private SkillsDAO skillsDAO;
    private AuditLogService auditLogService;

    EmployeeSkillService()
    {
        this.employeeSkillDAO = new EmployeeSkillDAO();
        this.employeeDAO = new EmployeeDAO();
        this.skillsDAO = new SkillsDAO();
        this.auditLogService = new AuditLogService();
    }

    public boolean assignSkillToEmployee(int emp_id, int skill_id, int proficiency) throws SQLException
    {
        if(emp_id <= 0 || skill_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "ASSIGN_SKILL_TO_EMPLOYEE", "EMPLOYEE_SKILL", skill_id, SessionManager.getUser().getName()+" added a new skill to "+employeeDAO.getEmployeeById(emp_id).getName());

        return employeeSkillDAO.addSkillToEmployee(emp_id, skill_id, proficiency);
    }

    public boolean removeSkillFromEmployee(int emp_id, int skill_id) throws SQLException
    {
        if(emp_id <= 0 || skill_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "REMOVE_SKILL_FROM_EMPLOYEE", "EMPLOYEE_SKILL", skill_id, SessionManager.getUser().getName()+" removed a skill from "+employeeDAO.getEmployeeById(emp_id).getName());

        return employeeSkillDAO.removeSkillFromEmployee(emp_id, skill_id) == 1;
    }

    public List<Skills> getSkillsByEmployee(int emp_id) throws SQLException
    {
        if(emp_id <= 0)
            return null;

        return employeeSkillDAO.getSkillsByEmployee(emp_id);
    }

    public List<Employee> getEmployeesBySkill(int skill_id) throws SQLException
    {
        if(skill_id <= 0)
            return null;

        return employeeSkillDAO.getEmployeesBySkill(skill_id);
    }
    public List<Employee> getEmployeesByMultipleSkills(List<Integer> skill_ids) throws SQLException
    {
        if(skill_ids == null || skill_ids.isEmpty())
            return null;

        List<Employee> result = new LinkedList<>();

        List<Employee> allEmployees = employeeDAO.getAllEmployees();

        for(Employee emp : allEmployees)
        {
            List<Skills> empSkills = employeeSkillDAO.getSkillsByEmployee(emp.getEmp_id());

            boolean hasAllSkills = true;
            for(Integer skillId : skill_ids)
            {
                boolean found = false;
                for(Skills s : empSkills)
                {
                    if(s.getSkill_id() == skillId)
                    {
                        found = true;
                        break;
                    }
                }

                if(!found)
                {
                    hasAllSkills = false;
                    break;
                }
            }

            if(hasAllSkills)
                result.add(emp);
        }

        return result;
    }

    public boolean updateEmployeeSkills(int emp_id, List<Integer> skill_ids) throws SQLException
    {
        if(emp_id <= 0)
            return false;

        employeeSkillDAO.deleteAllSkillsOfEmployee(emp_id);

        for(Integer skill_id : skill_ids)
        {
            employeeSkillDAO.addSkillToEmployee(emp_id, skill_id, 5);
        }

        auditLogService.logAction(SessionManager.getUser().getUserId(), "UPDATE_EMPLOYEE_SKILLS", "EMPLOYEE_SKILL", emp_id, SessionManager.getUser().getName()+" updated the skills of "+employeeDAO.getEmployeeById(emp_id).getName());

        return true;
    }
}
