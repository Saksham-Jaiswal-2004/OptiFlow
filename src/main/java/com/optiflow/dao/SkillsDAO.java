package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Skills;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SkillsDAO
{
    public void createSkill(String name, String description) throws SQLException
    {
        String sql = "INSERT INTO skills (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();
        }
    }

    public List<Skills> getAllSkills() throws SQLException
    {
        LinkedList<Skills> skillList = new LinkedList<>();
        String sql = "SELECT * FROM skills";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Skills skill = new Skills();
                skill.setName(rs.getString("name"));
                skill.setDescription(rs.getString("description"));

                skillList.add(skill);
                return skillList;
            }
        }

        return null;
    }

    public List<Skills> getSkillById(int skill_id) throws SQLException
    {
        LinkedList<Skills> skillList = new LinkedList<>();
        String sql = "SELECT * FROM skills WHERE skill_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, skill_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Skills skill = new Skills();
                skill.setName(rs.getString("name"));
                skill.setDescription(rs.getString("description"));

                skillList.add(skill);
                return skillList;
            }
        }

        return null;
    }

    public int deleteSkill(int skill_id) throws SQLException
    {
        String sql = "DELETE FROM skills WHERE skill_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, skill_id);

            rs = stmt.executeUpdate();
        }

        return rs;
    }

//    Employee Skill DAO
//    public boolean addSkillToEmployee(int empId, int skill_id, int proficiency) throws SQLException
//    {}
//
//    Employee Skill DAO
//    public List<Skills> getSkillsByEmployee(int empId) throws SQLException
//    {}
//
//    Employee Skill DAO
//    public List<Employee> getEmployeesBySkill(int skillId) throws SQLException
//    {}
//
//    Employee Skill DAO
//    public boolean updateProficiency(int empId, int skillId, int proficiency) throws SQLException
//    {}
//
//    Employee Skill DAO
//    public boolean removeSkillFromEmployee(int empId, int skillId) throws SQLException
//    {}
}
