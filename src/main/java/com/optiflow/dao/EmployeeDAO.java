package com.optiflow.dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import com.optiflow.database.DBConnection;
import com.optiflow.models.Employee;
import com.optiflow.models.User;

public class EmployeeDAO
{
    public boolean addEmployee(int user_id, String name, String skill, String designation, String department, int manager_id, String status, int weeklyCapacity) throws SQLException
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
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Employee getEmployeeById(int emp_id) throws SQLException
    {
        String sql = "SELECT * FROM Employees WHERE employee_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, emp_id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next())
            {
                User user = new User();
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
                emp.setSkill(rs.getString("skills"));
                emp.setDesignation(rs.getString("designation"));
                emp.setDepartment(rs.getString("department"));
                emp.setManager_id(rs.getInt("manager_id"));
                emp.setStatus(rs.getString("status"));
                emp.setWeeklyCapacity(rs.getInt("weekly_capacity"));
                return emp;
            }
        }

        return null;
    }

    public Employee getEmployeeByUserId(int userId) throws SQLException
    {
        String sql = "SELECT * FROM Employees WHERE user_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if(rs.next())
            {
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
                emp.setSkill(rs.getString("skills"));
                emp.setDesignation(rs.getString("designation"));
                emp.setDepartment(rs.getString("department"));
                emp.setManager_id(rs.getInt("manager_id"));
                emp.setStatus(rs.getString("status"));
                emp.setWeeklyCapacity(rs.getInt("weekly_capacity"));
                return emp;
            }
        }

        return null;
    }

    public List<Employee> getAllEmployees() throws SQLException
    {
        LinkedList<Employee> empList = new LinkedList<>();
        String sql = "SELECT * FROM employees";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
                emp.setSkill(rs.getString("skills"));
                emp.setDesignation(rs.getString("designation"));
                emp.setDepartment(rs.getString("department"));
                emp.setManager_id(rs.getInt("manager_id"));
                emp.setStatus(rs.getString("status"));
                emp.setWeeklyCapacity(rs.getInt("weekly_capacity"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));

                empList.add(emp);
            }
        }
        return empList;
    }

    public List<Employee> getEmployeesByManager(int managerId) throws SQLException
    {
        LinkedList<Employee> empList = new LinkedList<>();
        String sql = "SELECT * FROM employees WHERE manager_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, managerId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
                emp.setSkill(rs.getString("skills"));
                emp.setDesignation(rs.getString("designation"));
                emp.setDepartment(rs.getString("department"));
                emp.setManager_id(rs.getInt("manager_id"));
                emp.setStatus(rs.getString("status"));
                emp.setWeeklyCapacity(rs.getInt("weekly_capacity"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));

                empList.add(emp);
            }
        }
        return empList;
    }

    public List<Employee> getEmployeesByDepartment(String department) throws SQLException
    {
        LinkedList<Employee> empList = new LinkedList<>();
        String sql = "SELECT * FROM employees WHERE department=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
                emp.setSkill(rs.getString("skills"));
                emp.setDesignation(rs.getString("designation"));
                emp.setDepartment(rs.getString("department"));
                emp.setManager_id(rs.getInt("manager_id"));
                emp.setStatus(rs.getString("status"));
                emp.setWeeklyCapacity(rs.getInt("weekly_capacity"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));

                empList.add(emp);
            }
        }
        return empList;
    }

    public List<Employee> getEmployeesByStatus(String status) throws SQLException
    {
        LinkedList<Employee> empList = new LinkedList<>();
        String sql = "SELECT * FROM employees WHERE status=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
                emp.setSkill(rs.getString("skills"));
                emp.setDesignation(rs.getString("designation"));
                emp.setDepartment(rs.getString("department"));
                emp.setManager_id(rs.getInt("manager_id"));
                emp.setStatus(rs.getString("status"));
                emp.setWeeklyCapacity(rs.getInt("weekly_capacity"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));

                empList.add(emp);
            }
        }
        return empList;
    }

    public int getWeeklyCapacity(int emp_id)
    {
        if(emp_id <= 0)
            return -1;

        return getWeeklyCapacity(emp_id);
    }

    public int getAllocatedHours(int emp_id)
    {
        if(emp_id <= 0)
            return -1;

        return getAllocatedHours(emp_id);
    }

    public int updateName(int emp_id, String name) throws SQLException
    {
        String sql = "UPDATE employees SET name=? WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, name);
            stmt.setInt(2, emp_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

//  Skills Needed
    public int updateSkill(int emp_id, String name) throws SQLException
    {
        return -1;
    }

    public int updateDesignation(int emp_id, String designation) throws SQLException
    {
        String sql = "UPDATE employees SET designation=? WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, designation);
            stmt.setInt(2, emp_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateDepartment(int emp_id, String department) throws SQLException
    {
        String sql = "UPDATE employees SET department=? WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, department);
            stmt.setInt(2, emp_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateManager(int emp_id, int managerId) throws SQLException
    {
        String sql = "UPDATE employees SET manager_id=? WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, managerId);
            stmt.setInt(2, emp_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateStatus(int emp_id, String status) throws SQLException
    {
        String sql = "UPDATE employees SET status=? WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, status);
            stmt.setInt(2, emp_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateWeeklyCapacity(int emp_id, int weekly_capacity) throws SQLException
    {
        String sql = "UPDATE employees SET weekly_capacity=? WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, weekly_capacity);
            stmt.setInt(2, emp_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int deleteEmployee(int emp_id) throws SQLException
    {
        String sql = "DELETE FROM employees WHERE employee_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, emp_id);

            rs = stmt.executeUpdate();
        }

        return rs;
    }
}
