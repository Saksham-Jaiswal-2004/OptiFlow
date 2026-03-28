package com.optiflow.services;

import com.optiflow.dao.TaskSkillDAO;
import com.optiflow.models.TaskSkill;
import com.optiflow.models.User;
import com.optiflow.utils.AuthorizationUtil;

import java.sql.*;
import java.util.List;

public class TaskSkillService
{
    private TaskSkillDAO taskSkillDAO;

    public TaskSkillService()
    {
        this.taskSkillDAO = new TaskSkillDAO();
    }

    public boolean addSkillToTask(User user, int task_id, int skill_id) throws SQLException
    {
        if(!AuthorizationUtil.isManager(user) && !AuthorizationUtil.isAdmin(user))
            return false;

        if(task_id <= 0 || skill_id <= 0)
            return false;

        return taskSkillDAO.addSkillToTask(task_id, skill_id);
    }

    public boolean removeSkillFromTask(User user, int task_id, int skill_id) throws SQLException
    {

        if(!AuthorizationUtil.isManager(user) && !AuthorizationUtil.isAdmin(user))
            return false;

        return taskSkillDAO.removeSkillFromTask(task_id, skill_id) == 1;
    }

    public List<TaskSkill> getSkillsForTask(int task_id) throws SQLException
    {

        if(task_id <= 0)
            return null;

        return taskSkillDAO.getSkillsByTask(task_id);
    }

    public List<TaskSkill> getTasksBySkill(int skill_id) throws SQLException
    {

        if(skill_id <= 0)
            return null;

        return taskSkillDAO.getTasksBySkill(skill_id);
    }
}
