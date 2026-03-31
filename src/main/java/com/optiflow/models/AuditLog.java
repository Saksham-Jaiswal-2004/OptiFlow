package com.optiflow.models;

import java.sql.Date;

public class AuditLog
{
    private int log_id;
    private int user_id;
    private String user_role;
    private String action;
    private String entityType;
    private int entity_id;
    private String details;
    private Date date;

    public AuditLog()
    {
        System.out.println("Mai Audit Logs Hu!");
    }

    public AuditLog(int user_id, String action, String entityType, int entity_id, String details)
    {
        this.user_id = user_id;
        this.action = action;
        this.entityType = entityType;
        this.entity_id = entity_id;
        this.details = details;
    }

    public int getLog_id()
    {
        return log_id;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public String getUser_role()
    {
        return user_role;
    }

    public String getAction()
    {
        return action;
    }

    public String getEntityType()
    {
        return entityType;
    }

    public int getEntity_id()
    {
        return entity_id;
    }

    public String getDetails()
    {
        return details;
    }

    public Date getDate()
    {
        return date;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public void setUser_role(String role)
    {
        this.user_role = role;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    public void setEntity_id(int entity_id)
    {
        this.entity_id = entity_id;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
