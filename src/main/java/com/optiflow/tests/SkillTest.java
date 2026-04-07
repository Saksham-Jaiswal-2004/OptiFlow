package com.optiflow.tests;

import com.optiflow.models.Skills;
import com.optiflow.services.SkillService;

import java.util.List;
import java.util.Scanner;

public class SkillTest
{
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        SkillService skillService = new SkillService();

        System.out.println("********    Employee Skill Test    ********");
        System.out.println("1. Add a Skill");
        System.out.println("2. Get All Skills");
        System.out.println("3. Delete Skills");
        System.out.print("Enter your choice: ");
        int ch = sc.nextInt();
        sc.nextLine();

        switch (ch)
        {
            case 1: while(true)
                {
                    System.out.print("\nEnter Skill Name: ");
                    String skill = sc.nextLine();
                    System.out.print("Enter Skill Description: ");
                    String description = sc.nextLine();

                    if(skill.equalsIgnoreCase("exit"))
                        break;

                    Skills skills = new Skills(skill, description);
                    skillService.addSkill(skills);
                }
            break;
            case 2: List<Skills> skillsList = skillService.getAllSkills();
                for(Skills sk: skillsList)
                {
                    System.out.println(sk.getSkill_id()+". "+sk.getName()+" - "+sk.getDescription());
                }
            break;
            case 3: System.out.print("Enter Skill-Id to delete: ");
                int id = sc.nextInt();
                skillService.deleteSkill(id);
            break;
            default: System.out.println("Invalid Choice!");
        }
    }
}
