package com.optiflow.models;

public class ProjectSkill
{
    private int id;
    private int project_id;
    private int skill_id;

    public ProjectSkill()
    {}

    public ProjectSkill(int project_id, int skill_id)
    {
        this.project_id = project_id;
        this.skill_id = skill_id;
    }

    public int getId()
    {
        return id;
    }

    public int getProjectId()
    {
        return project_id;
    }

    public int getSkillId()
    {
        return skill_id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setProjectId(int project_id)
    {
        this.project_id = project_id;
    }

    public void setSkillId(int skill_id) {
        this.skill_id = skill_id;
    }
}
