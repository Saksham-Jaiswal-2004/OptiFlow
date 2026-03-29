package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Employee;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

    public String exportEmployeesToCSV() throws Exception
    {
        List<Employee> employees = employeeDAO.getAllEmployees();

        String fileName = "employees_" + LocalDate.now() + ".csv";

        FileWriter writer = new FileWriter(fileName);

        writer.append("User-ID,Emp-ID,Name,Department,Designation,Status,Manager-ID,Weekly Capacity,Allocated Hours\n");

        for (Employee e : employees)
        {
            writer.append(e.getUser_id() + ",")
                    .append(e.getEmp_id() + ",")
                    .append(e.getName() + ",")
                    .append(e.getDepartment() + ",")
                    .append(e.getDesignation() + ",")
                    .append(e.getStatus() + ",")
                    .append(e.getManager_id() + ",")
                    .append(e.getWeeklyCapacity() + ",")
                    .append(e.getAllocated_hours() + "\n");
        }

        writer.flush();
        writer.close();

        return fileName;
    }

    public String exportEmployeesToExcel() throws Exception
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");

        int rowNum = 1;

        for(Employee e : employeeDAO.getAllEmployees()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getEmp_id());
            row.createCell(1).setCellValue(e.getName());
        }

        FileOutputStream fileOut = new FileOutputStream("employees.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        return "";
    }
}
