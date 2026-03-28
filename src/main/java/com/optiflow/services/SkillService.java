package com.optiflow.services;

import com.optiflow.dao.SkillsDAO;
import com.optiflow.models.Employee;
import com.optiflow.models.Skills;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SkillService
{
    private SkillsDAO skillsDAO;

    SkillService()
    {
        this.skillsDAO = new SkillsDAO();
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

        return skillsDAO.deleteSkill(skill_id) == 1;
    }

}
