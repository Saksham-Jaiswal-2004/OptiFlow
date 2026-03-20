package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Employee;
import com.optiflow.models.EmployeeSkill;
import com.optiflow.models.Skills;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EmployeeSkillDAO
{
    public void addSkillToEmployee(int emp_id, int skill_id, int proficiency) throws SQLException
    {
        String sql = "INSERT INTO employee_skills (employee_id, skill_id, proficiency) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp_id);
            stmt.setInt(2, skill_id);
            stmt.setInt(3, proficiency);
            stmt.executeUpdate();
        }
    }

    public List<Skills> getSkillsByEmployee(int emp_id) throws SQLException
    {
        LinkedList<Skills> skillList = new LinkedList<>();
        LinkedList<EmployeeSkill> empSkillList = new LinkedList<>();
        String sql = "SELECT * FROM employee_skills WHERE employee_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emp_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                EmployeeSkill empSkill = new EmployeeSkill();
                empSkill.setEmp_id(rs.getInt("employee_id"));
                empSkill.setSkill_id(rs.getInt("skill_id"));
                empSkill.setProficiency(rs.getInt("proficiency"));
                empSkillList.add(empSkill);
            }

            for(EmployeeSkill empSkill: empSkillList)
            {
                SkillsDAO sd = new SkillsDAO();
                Skills sk = sd.getSkillById(empSkill.getSkill_id());
                skillList.add(sk);
            }

            return skillList;
        }
    }

    public List<Employee> getEmployeesBySkill(int skill_id) throws SQLException
    {
        LinkedList<Employee> empList = new LinkedList<>();
        LinkedList<EmployeeSkill> empSkillList = new LinkedList<>();
        String sql = "SELECT * FROM employee_skills WHERE skill_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, skill_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {EmployeeSkill empSkill = new EmployeeSkill();
                empSkill.setEmp_id(rs.getInt("employee_id"));
                empSkill.setSkill_id(rs.getInt("skill_id"));
                empSkill.setProficiency(rs.getInt("proficiency"));
                empSkillList.add(empSkill);
            }

            for(EmployeeSkill empSkill: empSkillList)
            {
                EmployeeDAO ed = new EmployeeDAO();
                Employee emp = ed.getEmployeeById(empSkill.getEmp_id());
                empList.add(emp);
            }

            return empList;
        }
    }

    public int updateProficiency(int emp_id, int skill_id, int proficiency) throws SQLException
    {
        String sql = "UPDATE employee_skills SET proficiency=? WHERE (employee_id, skill_id) VALUES (?, ?) ";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, proficiency);
            stmt.setInt(2, emp_id);
            stmt.setInt(3, skill_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int removeSkillFromEmployee(int emp_id, int skill_id) throws SQLException
    {
        String sql = "DELETE FROM employee_skills WHERE (employee_id, skill_id) VALUES (?, ?)";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, emp_id);
            stmt.setInt(2, skill_id);

            rs = stmt.executeUpdate();
        }

        return rs;
    }
}
