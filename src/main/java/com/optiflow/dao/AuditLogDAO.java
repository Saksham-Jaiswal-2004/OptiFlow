package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.AuditLog;
import com.optiflow.models.Comments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class AuditLogDAO
{
    public boolean addLog(int user_id, String action, String entityType, int entity_id, String details)
    {
        String sql = "INSERT INTO audit_logs (user_id, action, entityType, entitty_id, details) VALUES (?, ?, ?, ?, ?)";
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
        String sql = "SELECT * FROM audit_logs";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                AuditLog auditLog = new AuditLog();
                auditLog.setUser_id(rs.getInt("user_id"));
                auditLog.setAction(rs.getString("action"));
                auditLog.setEntityType(rs.getString("entityType"));
                auditLog.setEntity_id(rs.getInt("entity_id"));
                auditLog.setDetails(rs.getString("details"));

                auditLogs.add(auditLog);
                return auditLogs;
            }
        }

        return null;
    }

    public List<AuditLog> getLogsByUser(int user_id) throws SQLException
    {
        LinkedList<AuditLog> auditLogs = new LinkedList<>();
        String sql = "SELECT * FROM audit_logs WHERE user_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                AuditLog auditLog = new AuditLog();
                auditLog.setUser_id(rs.getInt("user_id"));
                auditLog.setAction(rs.getString("user_id"));
                auditLog.setEntityType(rs.getString("user_id"));
                auditLog.setEntity_id(rs.getInt("user_id"));
                auditLog.setDetails(rs.getString("user_id"));

                auditLogs.add(auditLog);
                return auditLogs;
            }
        }

        return null;
    }

    public List<AuditLog> getLogsByEntity(String entityType, int entity_id)
    {
        LinkedList<AuditLog> auditLogs = new LinkedList<>();
        String sql = "SELECT * FROM audit_logs WHERE (entityType, entity_id) VALUES (?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityType);
            stmt.setInt(2, entity_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                AuditLog auditLog = new AuditLog();
                auditLog.setUser_id(rs.getInt("user_id"));
                auditLog.setAction(rs.getString("user_id"));
                auditLog.setEntityType(rs.getString("user_id"));
                auditLog.setEntity_id(rs.getInt("user_id"));
                auditLog.setDetails(rs.getString("user_id"));

                auditLogs.add(auditLog);
                return auditLogs;
            }
        } catch (SQLException e) {
            return null;
        }

        return null;
    }
}
