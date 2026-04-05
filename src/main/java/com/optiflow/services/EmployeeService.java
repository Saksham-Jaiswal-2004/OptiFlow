package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.EmployeeSkillDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Employee;
import com.optiflow.models.ProjectSkill;
import com.optiflow.models.Skills;
import com.optiflow.models.User;
import com.optiflow.utils.SessionManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeeService
{
    private EmployeeDAO employeeDAO;
    private TaskDAO taskDAO;
    private AuditLogService auditLogService;
    private EmployeeSkillDAO employeeSkillDAO;

    public EmployeeService()
    {
        this.employeeDAO = new EmployeeDAO();
        this .taskDAO = new TaskDAO();
        this.auditLogService = new AuditLogService();
        this.employeeSkillDAO = new EmployeeSkillDAO();
    }

    public boolean createEmployee(Employee emp) throws SQLException
    {
        if(emp==null)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "CREATE_EMPLOYEE", "EMPLOYEE", emp.getUser_id(), SessionManager.getUser().getName()+" added a new employee");

        return employeeDAO.addEmployee(emp.getUser_id(), emp.getName(), emp.getDesignation(), emp.getDepartment(), emp.getManager_id(), emp.getStatus(), emp.getWeeklyCapacity());
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

    public List<Employee> getAllManagers() throws SQLException
    {
        return employeeDAO.getAllManagers();
    }

    public double calculateManagerWorkload(Employee manager) throws SQLException
    {
        int allocated = 0;
        int capacity = 0;

        List<Employee> empList = employeeDAO.getEmployeesByManager(manager.getUser_id());
        for(Employee emp: empList)
        {
            allocated += emp.getAllocated_hours();
            capacity += emp.getWeeklyCapacity();
        }

        if (capacity == 0)
            return 0;

        double score = 1 - ((double) allocated / capacity);

        return Math.max(score, 0);
    }

    public double calculateTeamStrength(Employee manager, List<ProjectSkill> requiredSkills) throws SQLException
    {
        List<Employee> team = employeeDAO.getEmployeesByManager(manager.getUser_id());

        if (team.isEmpty())
            return 0;

        int capableEmployees = 0;

        for (Employee emp : team)
        {
            List<Skills> empSkills = employeeSkillDAO.getSkillsByEmployee(emp.getEmp_id());

            for (Skills s : empSkills)
            {
                for (ProjectSkill ps : requiredSkills)
                {
                    if (s.getSkill_id() == ps.getSkillId())
                    {
                        capableEmployees++;
                        break;
                    }
                }
            }
        }

        return (double) capableEmployees / team.size();
    }

    private double calculateManagerSkillScore(Employee manager, List<ProjectSkill> requiredSkills) throws SQLException
    {
        List<Skills> managerSkills = employeeSkillDAO.getSkillsByEmployee(manager.getEmp_id());

        Set<Integer> skillIds = new HashSet<>();
        for (Skills s : managerSkills)
            skillIds.add(s.getSkill_id());

        int match = 0;
        for (ProjectSkill ps : requiredSkills)
        {
            if (skillIds.contains(ps.getSkillId()))
                match++;
        }

        return requiredSkills.isEmpty() ? 0 : (double) match / requiredSkills.size();
    }

    public double calculateManagerScore(Employee manager, List<ProjectSkill> requiredSkills) throws SQLException
    {
        double skillScore = calculateManagerSkillScore(manager, requiredSkills);
        double teamStrength = calculateTeamStrength(manager, requiredSkills);
        double workloadScore = calculateManagerWorkload(manager);
        double performanceScore = employeeDAO.getManagerPerformance(manager.getEmp_id());

        return (0.35 * skillScore) + (0.30 * teamStrength) + (0.20 * workloadScore) + (0.15 * performanceScore);
    }

    public boolean updateEmployee(Employee emp) throws SQLException
    {
        if(emp == null)
            return false;

        try {
            employeeDAO.updateName(emp.getEmp_id(), emp.getName());
            employeeDAO.updateDesignation(emp.getEmp_id(), emp.getDesignation());
            employeeDAO.updateDepartment(emp.getEmp_id(), emp.getDepartment());
            employeeDAO.updateManager(emp.getEmp_id(), emp.getManager_id());
            employeeDAO.updateStatus(emp.getEmp_id(), emp.getStatus());
            employeeDAO.updateWeeklyCapacity(emp.getEmp_id(), emp.getWeeklyCapacity());

            auditLogService.logAction(SessionManager.getUser().getUserId(), "UPDATE_EMPLOYEE", "EMPLOYEE", emp.getEmp_id(), SessionManager.getUser().getName()+" updated employee details for "+emp.getName());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteEmployee(int emp_id) throws SQLException
    {
        if(emp_id<=0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "DELETE_EMPLOYEE", "EMPLOYEE", emp_id, SessionManager.getUser().getName()+" deleted employee "+getEmployeeById(emp_id).getName());

        employeeSkillDAO.deleteAllSkillsOfEmployee(emp_id);

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

        auditLogService.logAction(SessionManager.getUser().getUserId(), "ASSIGN_MANAGER", "EMPLOYEE", emp_id, SessionManager.getUser().getName()+" assigned a manager to "+getEmployeeById(emp_id).getName());

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

        auditLogService.logAction(SessionManager.getUser().getUserId(), "EXPORT_EMPLOYEE", "EMPLOYEE", -1, SessionManager.getUser().getName()+" exported employee details via CSV");

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

        auditLogService.logAction(SessionManager.getUser().getUserId(), "EXPORT_EMPLOYEE", "EMPLOYEE", -1, SessionManager.getUser().getName()+" exported employee details via Excel");

        return "";
    }
}
