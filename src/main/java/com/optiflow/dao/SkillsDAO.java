package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Skills;
import com.optiflow.services.AuditLogService;
import com.optiflow.utils.SessionManager;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SkillsDAO
{
    private AuditLogService auditLogService;

    public SkillsDAO()
    {
        this.auditLogService = new AuditLogService();
    }

    public boolean createSkill(String name, String description) throws SQLException
    {
        String sql = "INSERT INTO skills (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);

            int rows = stmt.executeUpdate();

            if (rows > 0)
            {
                ResultSet rs = stmt.getGeneratedKeys();

                if (rs.next())
                {
                    int generatedId = rs.getInt(1);
                    auditLogService.logAction(SessionManager.getUser().getUserId(), "ADD_SKILL", "SKILL", generatedId, SessionManager.getUser().getName()+" addeded a new skill to database");
                }
            }

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Skills> getAllSkills() throws SQLException
    {
        LinkedList<Skills> skillList = new LinkedList<>();
        String sql = "SELECT * FROM skills";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Skills skill = new Skills();
                skill.setSkill_id(rs.getInt("skill_id"));
                skill.setName(rs.getString("name"));
                skill.setDescription(rs.getString("description"));

                skillList.add(skill);
            }
                return skillList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Skills getSkillById(int skill_id) throws SQLException
    {
        String sql = "SELECT * FROM skills WHERE skill_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, skill_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Skills skill = new Skills();
                skill.setName(rs.getString("name"));
                skill.setDescription(rs.getString("description"));

                return skill;
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
}
