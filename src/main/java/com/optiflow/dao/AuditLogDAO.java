package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.AuditLog;
import com.optiflow.models.Comments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class AuditLogDAO
{
    public AuditLogDAO()
    {}

    public boolean addLog(int user_id, String action, String entityType, int entity_id, String details)
    {
        String sql = "INSERT INTO auditlog (user_id, action, entityType, entity_id, details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, action);
            stmt.setString(3, entityType);
            stmt.setInt(4, entity_id);
            stmt.setString(5, details);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<AuditLog> getAllLogs() throws SQLException

    {
        LinkedList<AuditLog> auditLogs = new LinkedList<>();
        String sql = "SELECT * FROM auditlog";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                AuditLog auditLog = new AuditLog();
                auditLog.setUser_id(rs.getInt("user_id"));
//                auditLog.setUser_role(userDAO.getUserById(rs.getInt("user_id")).getRole());
                auditLog.setUser_role("Test");
                auditLog.setAction(rs.getString("action"));
                auditLog.setEntityType(rs.getString("entityType"));
                auditLog.setEntity_id(rs.getInt("entity_id"));
                auditLog.setDetails(rs.getString("details"));
                auditLog.setDate(java.sql.Date.valueOf(rs.getDate("created_at").toLocalDate()));

                auditLogs.add(auditLog);
            }
            System.out.println("Logs: "+auditLogs);
            return auditLogs;
        }
    }

    public List<AuditLog> getLogsByUser(int user_id) throws SQLException
    {
        LinkedList<AuditLog> auditLogs = new LinkedList<>();
        String sql = "SELECT * FROM auditlog WHERE user_id=?";
        UserDAO userDAO = new UserDAO();

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                AuditLog auditLog = new AuditLog();
                auditLog.setUser_id(rs.getInt("user_id"));
                auditLog.setUser_role(userDAO.getUserById(rs.getInt("user_id")).getRole());
                auditLog.setAction(rs.getString("action"));
                auditLog.setEntityType(rs.getString("entityType"));
                auditLog.setEntity_id(rs.getInt("entity_id"));
                auditLog.setDetails(rs.getString("details"));
                auditLog.setDate(java.sql.Date.valueOf(rs.getDate("created_at").toLocalDate()));

                auditLogs.add(auditLog);
            }

            return auditLogs;
        }
    }

    public List<AuditLog> getLogsByEntity(String entityType, int entity_id)
    {
        LinkedList<AuditLog> auditLogs = new LinkedList<>();
        String sql = "SELECT * FROM auditlog WHERE entityType=? AND entity_id=?";
        UserDAO userDAO = new UserDAO();

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityType);
            stmt.setInt(2, entity_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                AuditLog auditLog = new AuditLog();
                auditLog.setUser_id(rs.getInt("user_id"));
                auditLog.setUser_role(userDAO.getUserById(rs.getInt("user_id")).getRole());
                auditLog.setAction(rs.getString("action"));
                auditLog.setEntityType(rs.getString("entityType"));
                auditLog.setEntity_id(rs.getInt("entity_id"));
                auditLog.setDetails(rs.getString("details"));
                auditLog.setDate(java.sql.Date.valueOf(rs.getDate("created_at").toLocalDate()));

                auditLogs.add(auditLog);
            }

            return auditLogs;
        } catch (SQLException e) {
            return null;
        }
    }
}
