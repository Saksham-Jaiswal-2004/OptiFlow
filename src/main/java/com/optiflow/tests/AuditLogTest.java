package com.optiflow.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optiflow.models.AuditLog;
import com.optiflow.services.AuditLogService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AuditLogTest
{
    public static void main(String[] args) throws JsonProcessingException, SQLException
    {
        Scanner sc = new Scanner(System.in);
        AuditLogService auditLogService = new AuditLogService();

        System.out.println("********    Audit Log Test    ********");
        System.out.println("1. Log an Audit");
        System.out.println("2. Get all Audits");
        System.out.println("3. Get Audits by User-ID");
        System.out.println("4. Get Audits by Entity Type and ID");
        System.out.print("Enter your choice: ");
        int ch = sc.nextInt();

        switch (ch)
        {
            case 1: auditLogService.logAction(007, "TEST_LOG", "TEST", 007, "This is a test audit log");
            break;
            case 2:
                List<AuditLog> auditLogList = auditLogService.getAllLogs();
                for(AuditLog auditLog: auditLogList)
                    System.out.println(auditLog.getDetails()+"\n");
            break;
            case 3: System.out.print("Enter User-Id: ");
                int uid = sc.nextInt();
                List<AuditLog> auditLogsList = auditLogService.getLogsByUser(uid);
                for(AuditLog auditLog: auditLogsList)
                    System.out.println(auditLog.getDetails()+"\n");
            break;
            case 4: List<AuditLog> auditLogs = auditLogService.getLogsByEntity("TEST", 007);
                for(AuditLog auditLog: auditLogs)
                    System.out.println(auditLog.getDetails()+"\n");
            break;
            default: System.out.println("Invalid Input!");
        }
    }
}
