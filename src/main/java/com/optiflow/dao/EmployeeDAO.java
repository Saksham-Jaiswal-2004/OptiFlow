package com.optiflow.dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import com.optiflow.database.DBConnection;
import com.optiflow.models.Employee;
import com.optiflow.models.User;

public class EmployeeDAO
{
    private ProjectDAO projectDAO;

    public EmployeeDAO()
    {
        this.projectDAO = new ProjectDAO();
    }

    public boolean addEmployee(int user_id, String name, String designation, String department, int manager_id, String status, int weeklyCapacity) throws SQLException
    {
        String sql = "INSERT INTO employees (user_id, name, designation, department, manager_id, status, weekly_capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, name);
            stmt.setString(3, designation);
            stmt.setString(4, department);
            stmt.setInt(5, manager_id);
            stmt.setString(6, status);
            stmt.setInt(7, weeklyCapacity);
            stmt.executeUpdate();
            System.out.println("Employee Added!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Employee getEmployeeById(int emp_id) throws SQLException
    {
        String sql = "SELECT * FROM employees WHERE employee_id=?";

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
        String sql = "SELECT * FROM employees WHERE user_id=?";

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

    public List<Employee> getAllManagers() throws SQLException
    {
        LinkedList<Employee> empList = new LinkedList<>();
        String sql = "SELECT * FROM employees WHERE manager_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, 11);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Employee emp = new Employee();
                emp.setEmp_id(rs.getInt("employee_id"));
                emp.setUser_id(rs.getInt("user_id"));
                emp.setName(rs.getString("name"));
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

    public double getTeamUtilization(int managerId) throws SQLException
    {
        List<Employee> team = getEmployeesByManager(managerId);

        if (team.isEmpty()) return 0;

        double totalUtilization = 0;

        for (Employee emp : team)
        {
            int allocated = getAllocatedHours(emp.getEmp_id());
            int capacity = getWeeklyCapacity(emp.getEmp_id());

            if (capacity == 0) continue;

            totalUtilization += (double) allocated / capacity;
        }

        return totalUtilization / team.size();
    }

    public double getManagerPerformance(int managerId) throws SQLException
    {
        double completionRate = projectDAO.getProjectCompletionRate(managerId);
        double onTimeRate = projectDAO.getOnTimeDeliveryRate(managerId);
        double teamUtilization = getTeamUtilization(managerId);

        return (0.43 * completionRate) + (0.33 * onTimeRate) + (0.24 * teamUtilization);
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
//    public int updateSkill(int emp_id, String name) throws SQLException
//    {
//        return -1;
//    }

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

    public static void main(String[] args) throws SQLException
    {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        List<Employee> employeeList = employeeDAO.getAllEmployees();

        for(Employee e: employeeList)
            System.out.println(e.getName());
    }
}
