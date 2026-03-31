package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.TaskSkill;

import java.sql.*;
import java.util.*;

public class TaskSkillDAO
{
    private Connection conn;

    public TaskSkillDAO()
    {
        conn = DBConnection.getConnection();
    }

    public boolean addSkillToTask(int task_id, int skill_id) throws SQLException
    {
        String query = "INSERT INTO taskskill (task_id, skill_id) VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, task_id);
        stmt.setInt(2, skill_id);

        return stmt.executeUpdate() == 1;
    }

    public int removeSkillFromTask(int task_id, int skill_id) throws SQLException
    {
        String query = "DELETE FROM taskskill WHERE task_id = ? AND skill_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, task_id);
        stmt.setInt(2, skill_id);

        return stmt.executeUpdate();
    }

    public List<TaskSkill> getSkillsByTask(int task_id) throws SQLException
    {
        String query = "SELECT * FROM taskskill WHERE task_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, task_id);

        ResultSet rs = stmt.executeQuery();
        List<TaskSkill> list = new ArrayList<>();

        while(rs.next()) {
            TaskSkill ts = new TaskSkill();
            ts.setId(rs.getInt("id"));
            ts.setTaskId(rs.getInt("task_id"));
            ts.setSkillId(rs.getInt("skill_id"));

            list.add(ts);
        }

        return list;
    }

    public List<TaskSkill> getTasksBySkill(int skill_id) throws SQLException
    {
        String query = "SELECT * FROM taskskill WHERE skill_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, skill_id);

        ResultSet rs = stmt.executeQuery();
        List<TaskSkill> list = new ArrayList<>();

        while(rs.next())
        {
            TaskSkill ts = new TaskSkill();
            ts.setId(rs.getInt("id"));
            ts.setTaskId(rs.getInt("task_id"));
            ts.setSkillId(rs.getInt("skill_id"));

            list.add(ts);
        }

        return list;
    }
}
