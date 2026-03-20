package com.optiflow.models;

public class Skills
{
    private int skill_id;
    private String name;
    private String description;

    public Skills()
    {
        System.out.println("Mai Skills Hu!");;
    }

    public Skills(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public int getSkill_id()
    {
        return skill_id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
