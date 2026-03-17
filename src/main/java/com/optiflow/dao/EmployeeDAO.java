package com.optiflow.dao;

import java.sql.*;
import java.util.List;
import com.optiflow.database.DBConnection;
import com.optiflow.models.Employee;

public class EmployeeDAO
{
    public void addEmployee(int user_id, String name, String skill, String designation, String department, int manager_id, String status, int weeklyCapacity) throws SQLException
    {
        String sql = "INSERT INTO Employees (user_id, name, skills, designation, department, manager_id, status, weekly_capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, name);
            stmt.setString(3, skill);
            stmt.setString(4, designation);
            stmt.setString(5, department);
            if(manager_id == 0)
                stmt.setNull(6, java.sql.Types.INTEGER);
            else
                stmt.setInt(6, manager_id);
            stmt.setString(7, status);
            stmt.setInt(8, weeklyCapacity);
            stmt.executeUpdate();
        }
    }

    public Employee getEmployeeById(int empId)
    {}

    public Employee getEmployeeByUserId(int userId)
    {}

    public List<Employee> getAllEmployees()
    {}

    public List<Employee> getEmployeesByManager(int managerId)
    {}

    public List<Employee> getEmployeesByDepartment(String department)
    {}

    public List<Employee> getEmployeesByStatus(String status)
    {}

    public boolean updateEmployee(Employee emp)
    {}

    public boolean updateManager(int empId, int managerId)
    {}

    public boolean updateStatus(int empId, String status)
    {}

    public boolean deleteEmployee(int empId)
    {}

    public int getWeeklyCapacity(int empId)
    {}
}
