package com.optiflow.services;

import com.optiflow.models.Employee;
import java.util.LinkedList;
import java.util.List;

public class EmployeeService
{
    public boolean createEmployee(Employee emp)
    {
        return true;
    }

    public Employee getEmployeeById(int empId)
    {
        Employee e1 = new Employee();

        return e1;
    }

    public List<Employee> getAllEmployees()
    {
        LinkedList<Employee> empList = new LinkedList<>();

        return empList;
    }

    public boolean updateEmployee(Employee emp)
    {
        return true;
    }

    public boolean deleteEmployee(int empId)
    {
        return true;
    }

    public List<Employee> getEmployeesByManager(int managerId)
    {
        LinkedList<Employee> empList = new LinkedList<>();

        return empList;
    }

    public boolean assignManager(int empId, int managerId)
    {
        return true;
    }

    public int calculateCurrentWorkload(int empId)
    {
        return -1;
    }

    public int getAvailableCapacity(int empId)
    {
        return -1;
    }

    public boolean isEmployeeOverloaded(int empId)
    {
        return true;
    }

    public List<Employee> getAvailableEmployees()
    {
        LinkedList<Employee> empList = new LinkedList<>();

        return empList;
    }

    public Employee getBestEmployeeForTask(int requiredHours)
    {
        Employee e1 = new Employee();

        return e1;
    }

    public List<Employee> getEmployeesBySkill(String skill)
    {
        LinkedList<Employee> empList = new LinkedList<>();

        return empList;
    }

    public boolean updateSkills(int empId, String skills)
    {
        return true;
    }
}
