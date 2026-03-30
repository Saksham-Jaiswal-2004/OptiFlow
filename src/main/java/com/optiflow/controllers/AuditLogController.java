package com.optiflow.controllers;

public class AuditLogController
{
    public Object getAllLogs()
    {
        return true;
    }

    public Object getLogsByUser(int userId)
    {
        return true;
    }

    public Object getLogsByEntity(String entityType, int entityId)
    {
        return true;
    }

    public Object getLogsByDateRange(java.sql.Timestamp start, java.sql.Timestamp end)
    {
        return true;
    }
}
