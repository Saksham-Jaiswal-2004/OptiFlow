package com.optiflow.services;

import com.optiflow.dao.ProjectSkillDAO;
import com.optiflow.models.ProjectSkill;
import com.optiflow.models.User;
import com.optiflow.utils.AuthorizationUtil;

import java.sql.*;
import java.util.List;

public class ProjectSkillService
{
    private ProjectSkillDAO projectSkillDAO;

    ProjectSkillService()
    {
        this.projectSkillDAO = new ProjectSkillDAO();
    }

    public boolean addSkillToProject(User user, int project_id, int skill_id) throws SQLException
    {
        if(!AuthorizationUtil.isManager(user) && !AuthorizationUtil.isAdmin(user))
            return false;

        if(project_id <= 0 || skill_id <= 0)
            return false;

        return projectSkillDAO.addSkillToProject(project_id, skill_id);
    }

    public boolean removeSkillFromProject(User user, int project_id, int skill_id) throws SQLException
    {
        if(!AuthorizationUtil.isManager(user) && !AuthorizationUtil.isAdmin(user))
            return false;

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
