package com.optiflow.dao;

import java.sql.*;
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
}
