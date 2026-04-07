package com.optiflow.controllers;

import com.optiflow.models.AuditLog;
import com.optiflow.services.AuditLogService;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLogController
{
    private final AuditLogService auditLogService;

    public AuditLogController() {
        this.auditLogService = new AuditLogService();
    }

    public List<AuditLog> getAllLogs()
    {
        try {
            List<AuditLog> logs = auditLogService.getAllLogs();
            return logs == null ? Collections.emptyList() : logs;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<AuditLog> getLogsByUser(int userId)
    {
        try {
            List<AuditLog> logs = auditLogService.getLogsByUser(userId);
            return logs == null ? Collections.emptyList() : logs;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<AuditLog> getLogsByEntity(String entityType, int entityId)
    {
        try {
            List<AuditLog> logs = auditLogService.getLogsByEntity(entityType, entityId);
            return logs == null ? Collections.emptyList() : logs;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<AuditLog> getLogsByDateRange(Date start, Date end)
    {
        if (start == null || end == null || start.after(end)) {
            return Collections.emptyList();
        }

        return getAllLogs().stream()
            .filter(log -> log.getDate() != null)
            .filter(log -> !log.getDate().before(start) && !log.getDate().after(end))
                .collect(Collectors.toList());
    }
}
