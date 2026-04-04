package com.optiflow.services;

import com.optiflow.dao.SkillsDAO;
import com.optiflow.models.Employee;
import com.optiflow.models.Skills;
import com.optiflow.utils.SessionManager;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SkillService
{
    private SkillsDAO skillsDAO;
    private AuditLogService auditLogService;

    SkillService()
    {
        this.skillsDAO = new SkillsDAO();
        this.auditLogService = new AuditLogService();
    }

    public boolean addSkill(Skills skill) throws SQLException
    {
        if(skill == null)
            return false;

        return skillsDAO.createSkill(skill.getName(), skill.getDescription());
    }

    public List<Skills> getAllSkills() throws SQLException
    {
        return skillsDAO.getAllSkills();
    }

    public boolean deleteSkill(int skill_id) throws SQLException
    {
        if(skill_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "REMOVE_SKILL", "SKILL", SessionManager.getUser().getUserId(), SessionManager.getUser().getName()+" removed a skill from database");

        return skillsDAO.deleteSkill(skill_id) == 1;
    }

}
