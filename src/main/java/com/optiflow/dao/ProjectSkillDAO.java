package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.ProjectSkill;

import java.sql.*;
import java.util.*;

public class ProjectSkillDAO
{
    private Connection conn;

    public ProjectSkillDAO()
    {
        conn = DBConnection.getConnection();
    }

    public boolean addSkillToProject(int project_id, int skill_id) throws SQLException
    {
        String query = "INSERT INTO projectskill (project_id, skill_id) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, project_id);
        stmt.setInt(2, skill_id);

        return stmt.executeUpdate() == 1;
    }

    public boolean removeSkillFromProject(int project_id, int skill_id) throws SQLException
    {
        String query = "DELETE FROM projectskill WHERE project_id = ? AND skill_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, project_id);
        stmt.setInt(2, skill_id);

        return stmt.executeUpdate() == 1;
    }

    public List<ProjectSkill> getSkillsByProject(int project_id) throws SQLException
    {
        String query = "SELECT * FROM projectskill WHERE project_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, project_id);

        ResultSet rs = stmt.executeQuery();
        List<ProjectSkill> list = new ArrayList<>();

        while (rs.next())
        {
            ProjectSkill ps = new ProjectSkill();
            ps.setId(rs.getInt("id"));
            ps.setProjectId(rs.getInt("project_id"));
            ps.setSkillId(rs.getInt("skill_id"));

            list.add(ps);
        }

        return list;
    }

    public List<ProjectSkill> getProjectsBySkill(int skill_id) throws SQLException
    {
        String query = "SELECT * FROM projectskill WHERE skill_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, skill_id);

        ResultSet rs = stmt.executeQuery();
        List<ProjectSkill> list = new ArrayList<>();

        while (rs.next())
        {
            ProjectSkill ps = new ProjectSkill();
            ps.setId(rs.getInt("id"));
            ps.setProjectId(rs.getInt("project_id"));
            ps.setSkillId(rs.getInt("skill_id"));

            list.add(ps);
        }

        return list;
    }
}
