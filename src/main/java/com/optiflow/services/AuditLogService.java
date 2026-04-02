package com.optiflow.services;

import com.optiflow.dao.AuditLogDAO;
import com.optiflow.models.AuditLog;

import java.sql.SQLException;
import java.util.List;

public class AuditLogService
{
    private AuditLogDAO auditLogDAO;

    public AuditLogService()
    {
        this.auditLogDAO = new AuditLogDAO();
    }

    public void logAction(int user_id, String action, String entityType, int entity_id, String details)
    {
        try
        {
            auditLogDAO.addLog(user_id, action, entityType, entity_id, details);
            System.out.println("Audit Logged!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public List<AuditLog> getAllLogs() throws SQLException
    {
        return auditLogDAO.getAllLogs();
    }

    public List<AuditLog> getLogsByUser(int user_id) throws SQLException
    {
        if(user_id <= 0)
            return null;

        return auditLogDAO.getLogsByUser(user_id);
    }

    public List<AuditLog> getLogsByEntity(String entityType, int entity_id)
    {
        if(entity_id <= 0)
            return null;

        return auditLogDAO.getLogsByEntity(entityType, entity_id);
    }
}
