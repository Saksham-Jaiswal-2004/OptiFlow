package com.optiflow.controllers;

import com.optiflow.models.Skills;
import com.optiflow.services.SkillService;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

public class SkillController
{
    private final SkillService skillService;

    public SkillController() {
        this.skillService = createSkillService();
    }

    private SkillService createSkillService() {
        try {
            Constructor<SkillService> constructor = SkillService.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean addSkill(String name, String description)
    {
        if (skillService == null) {
            return false;
        }

        try {
            Skills skill = new Skills(name, description);
            return skillService.addSkill(skill);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSkill(int skillId)
    {
        if (skillService == null) {
            return false;
        }

        try {
            return skillService.deleteSkill(skillId);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Skills> getAllSkills()
    {
        if (skillService == null) {
            return Collections.emptyList();
        }

        try {
            List<Skills> skills = skillService.getAllSkills();
            return skills == null ? Collections.emptyList() : skills;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
