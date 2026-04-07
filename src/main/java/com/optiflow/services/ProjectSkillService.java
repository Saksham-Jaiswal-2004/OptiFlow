package com.optiflow.services;

import com.optiflow.dao.ProjectSkillDAO;
import com.optiflow.models.ProjectSkill;
import com.optiflow.models.User;
import com.optiflow.utils.AuthorizationUtil;
import com.optiflow.utils.SessionManager;

import java.sql.*;
import java.util.List;

public class ProjectSkillService
{
    private ProjectSkillDAO projectSkillDAO;
    private AuditLogService auditLogService;

    public ProjectSkillService()
    {
        this.projectSkillDAO = new ProjectSkillDAO();
        this.auditLogService = new AuditLogService();
    }

    public boolean addSkillToProject(User user, int project_id, int skill_id) throws SQLException
    {
        if(!AuthorizationUtil.isManager(user) && !AuthorizationUtil.isAdmin(user))
            return false;

        if(project_id <= 0 || skill_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "ADD_SKILLS_TO_PROJECT", "PROJECT_SKILLS", project_id, SessionManager.getUser().getName()+" added skills to project");

        return projectSkillDAO.addSkillToProject(project_id, skill_id);
    }

    public boolean removeSkillFromProject(User user, int project_id, int skill_id) throws SQLException
    {
        if(!AuthorizationUtil.isManager(user) && !AuthorizationUtil.isAdmin(user))
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "REMOVE_SKILLS_FROM_PROJECT", "PROJECT_SKILLS", project_id, SessionManager.getUser().getName()+" removed skills from project");

        return projectSkillDAO.removeSkillFromProject(project_id, skill_id);
    }

    public List<ProjectSkill> getSkillsForProject(int project_id) throws SQLException
    {
        if(project_id <= 0)
            return null;

        return projectSkillDAO.getSkillsByProject(project_id);
    }

    public List<ProjectSkill> getProjectsBySkill(int skill_id) throws SQLException
    {
        if(skill_id <= 0)
            return null;

        return projectSkillDAO.getProjectsBySkill(skill_id);
    }
}
