package com.optiflow.tests;

import com.optiflow.models.Employee;
import com.optiflow.models.Skills;
import com.optiflow.services.EmployeeSkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeSkillTest
{
    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        EmployeeSkillService employeeSkillService = new EmployeeSkillService();

        System.out.println("********    Employee Skill Test    ********");
        System.out.println("1. Assign Skill to Employee");
        System.out.println("2. Remove Skill from Employee");
        System.out.println("3. Get Skills by Employee");
        System.out.println("4. Get Employees by Skill");
        System.out.println("5. Get Employees by Multiple Skill");
        System.out.println("6. Update Employees Skills");
        System.out.print("Enter your choice: ");
        int ch = sc.nextInt();

        switch(ch)
        {
            case 1: System.out.print("Enter Employee Id: ");
                int empId = sc.nextInt();
                System.out.print("Enter Skill Id: ");
                int skillId = sc.nextInt();
                System.out.print("Enter Proficiency: ");
                int proficiency = sc.nextInt();

                if(employeeSkillService.assignSkillToEmployee(empId, skillId, proficiency))
                    System.out.println("Skill Added to Employee.");
            break;
            case 2: System.out.print("Enter Employee Id: ");
                empId = sc.nextInt();
                System.out.print("Enter Skill Id: ");
                skillId = sc.nextInt();

                if(employeeSkillService.removeSkillFromEmployee(empId, skillId))
                    System.out.println("Skill removed from Employee.");
            break;
            case 3: System.out.print("Enter Employee Id: ");
                empId = sc.nextInt();

                for(Skills skill : employeeSkillService.getSkillsByEmployee(empId))
                    System.out.println(skill.getName());
            break;
            case 4: System.out.print("Enter Skill Id: ");
                skillId = sc.nextInt();

                for(Employee employee : employeeSkillService.getEmployeesBySkill(skillId))
                    System.out.println(employee.getName());
            break;
            case 5: List<Integer> skillIds = new ArrayList<>();
                for(int i=0 ; i<3 ; i++)
                {
                    System.out.print("Enter Skill-Id "+i+": ");
                    int a = sc.nextInt();
                    skillIds.add(a);
                }

                System.out.println(employeeSkillService.getEmployeesByMultipleSkills(skillIds));
            break;
            case 6: System.out.print("Enter Employee Id: ");
                empId = sc.nextInt();
                    skillIds = new ArrayList<>();
                    for(int i=0 ; i<3 ; i++)
                    {
                        System.out.print("Enter Skill-Id "+i+": ");
                        int a = sc.nextInt();
                        skillIds.add(a);
                    }

                employeeSkillService.updateEmployeeSkills(empId, skillIds);
            break;
            default: System.out.println("Invalid Input!");
        }
    }
}
