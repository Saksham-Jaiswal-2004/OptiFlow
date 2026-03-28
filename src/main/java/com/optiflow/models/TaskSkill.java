package com.optiflow.models;

public class TaskSkill
{
    private int id;
    private int task_id;
    private int skill_id;

    public TaskSkill() {}

    public TaskSkill(int task_id, int skill_id)
    {
        this.task_id = task_id;
        this.skill_id = skill_id;
    }

    public int getId()
    {
        return id;
    }

    public int getTaskId()
    {
        return task_id;
    }

    public int getSkillId()
    {
        return skill_id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setTaskId(int taskId)
    {
        this.task_id = taskId;
    }

    public void setSkillId(int skillId)
    {
        this.skill_id = skillId;
    }
}
