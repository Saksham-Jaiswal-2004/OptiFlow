package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Employee;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EmployeeService
{
    private EmployeeDAO employeeDAO;
    private TaskDAO taskDAO;

    EmployeeService()
    {
        this.employeeDAO = new EmployeeDAO();
        this .taskDAO = new TaskDAO();
    }

    public boolean createEmployee(Employee emp) throws SQLException
    {
        if(emp==null)
            return false;

        return employeeDAO.addEmployee(emp.getUser_id(), emp.getName(), emp.getSkill(), emp.getDesignation(), emp.getDepartment(), emp.getManager_id(), emp.getStatus(), emp.getWeeklyCapacity());
    }

    public Employee getEmployeeById(int emp_id) throws SQLException
    {
        if(emp_id<=0)
            return null;

        return employeeDAO.getEmployeeById(emp_id);
    }

    public Employee getEmployeeByUserId(int user_id) throws SQLException
    {
        if(user_id<=0)
            return null;

        return employeeDAO.getEmployeeByUserId(user_id);
    }

    public List<Employee> getEmployeeByDepartment(String department) throws SQLException
    {
        if(department.isEmpty())
            return null;

        return employeeDAO.getEmployeesByDepartment(department);
    }

    public List<Employee> getAllEmployees() throws SQLException
    {
        return employeeDAO.getAllEmployees();
    }

    public boolean updateEmployee(Employee emp) throws SQLException
    {
        if(emp == null)
            return false;

        try {
            employeeDAO.updateName(emp.getEmp_id(), emp.getName());
            employeeDAO.updateSkill(emp.getEmp_id(), emp.getSkill());
            employeeDAO.updateDesignation(emp.getEmp_id(), emp.getDesignation());
            employeeDAO.updateDepartment(emp.getEmp_id(), emp.getDepartment());
            employeeDAO.updateManager(emp.getEmp_id(), emp.getManager_id());
            employeeDAO.updateStatus(emp.getEmp_id(), emp.getStatus());
            employeeDAO.updateWeeklyCapacity(emp.getEmp_id(), emp.getWeeklyCapacity());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteEmployee(int emp_id) throws SQLException
    {
        if(emp_id<=0)
            return false;

        return employeeDAO.deleteEmployee(emp_id) == 1;
    }

    public List<Employee> getEmployeesByManager(int manager_id) throws SQLException
    {
        if(manager_id<=0)
            return null;

        return employeeDAO.getEmployeesByManager(manager_id);
    }

    public boolean assignManager(int emp_id, int manager_id) throws SQLException
    {
        if(emp_id<=0 || manager_id<=0)
            return false;

        return employeeDAO.updateManager(emp_id, manager_id) == 1;
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
}
