package com.optiflow.controllers;

import com.optiflow.models.Employee;
import com.optiflow.services.EmployeeService;

import java.util.Collections;
import java.util.List;

public class EmployeeController
{
    private final EmployeeService employeeService;

    public EmployeeController() {
        this.employeeService = new EmployeeService();
    }

    public boolean createEmployee(int userId, String name, String skill, String designation, String department, int managerId, String status, int weeklyCapacity)
    {
        try {
            Employee emp = new Employee();
            emp.setUser_id(userId);
            emp.setName(name);
            emp.setDesignation(designation);
            emp.setDepartment(department);
            emp.setManager_id(managerId);
            emp.setStatus(status);
            emp.setWeeklyCapacity(weeklyCapacity);
            return employeeService.createEmployee(emp);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateEmployee(int empId, String name, String skill, String designation, String department, int managerId, String status, int weeklyCapacity)
    {
        try {
            Employee existing = employeeService.getEmployeeById(empId);
            if (existing == null) {
                return false;
            }

            existing.setName(name);
            existing.setDesignation(designation);
            existing.setDepartment(department);
            existing.setManager_id(managerId);
            existing.setStatus(status);
            existing.setWeeklyCapacity(weeklyCapacity);
            return employeeService.updateEmployee(existing);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteEmployee(int empId)
    {
        try {
            return employeeService.deleteEmployee(empId);
        } catch (Exception e) {
            return false;
        }
    }

    public Employee getEmployeeById(int empId)
    {
        try {
            return employeeService.getEmployeeById(empId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Employee> getAllEmployees()
    {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return employees == null ? Collections.emptyList() : employees;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Employee> getEmployeesByDepartment(String department)
    {
        try {
            List<Employee> employees = employeeService.getEmployeeByDepartment(department);
            return employees == null ? Collections.emptyList() : employees;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Employee> getEmployeesByManager(int managerId)
    {
        try {
            List<Employee> employees = employeeService.getEmployeesByManager(managerId);
            return employees == null ? Collections.emptyList() : employees;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean assignManager(int empId, int managerId)
    {
        try {
            return employeeService.assignManager(empId, managerId);
        } catch (Exception e) {
            return false;
        }
    }
}
