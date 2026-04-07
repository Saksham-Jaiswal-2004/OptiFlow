package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.Employee;
import com.optiflow.models.EmployeeSkill;
import com.optiflow.models.Skills;
import com.optiflow.services.EmployeeSkillService;
import com.optiflow.utils.SessionManager;

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
            case 1:
                UserDAO userDAO = new UserDAO();
                SessionManager.setUser(userDAO.getUserById(11));
                List<EmployeeSkill> employeeSkills = List.of(

                    // ================= TEAM 1 =================
                    new EmployeeSkill(2, 1, 5), // Rohit Verma - Java
                    new EmployeeSkill(2, 17, 4), // Spring Boot
                    new EmployeeSkill(2, 22, 4), // REST APIs
                    new EmployeeSkill(3, 3, 5), // Karan Mehta - JavaScript
                    new EmployeeSkill(3, 9, 4), // React.js
                    new EmployeeSkill(3, 14, 3), // Redux
                    new EmployeeSkill(3, 10, 2), // Next.js
                    new EmployeeSkill(4, 2, 5), // Priya Shah - Python
                    new EmployeeSkill(4, 25, 4), // Machine Learning
                    new EmployeeSkill(4, 28, 3), // TensorFlow
                    new EmployeeSkill(5, 6, 4), // Arjun Das - GoLang
                    new EmployeeSkill(5, 34, 3), // AWS
                    new EmployeeSkill(5, 35, 4), // Docker
                    new EmployeeSkill(6, 53, 4), // Neeraj Kumar - Selenium
                    new EmployeeSkill(6, 54, 4), // JUnit
                    new EmployeeSkill(7, 7, 4), // Simran Kaur - Kotlin
                    new EmployeeSkill(7, 41, 4), // Flutter
                    new EmployeeSkill(8, 50, 5), // Aditya Rao - Cybersecurity
                    new EmployeeSkill(8, 51, 4), // Penetration Testing
                    new EmployeeSkill(9, 1, 4), // Pooja Nair - Full Stack
                    new EmployeeSkill(9, 9, 3),
                    new EmployeeSkill(9, 17, 3),
                    new EmployeeSkill(9, 22, 3),

                    // ================= TEAM 2 =================
                    new EmployeeSkill(11, 3, 5), // Aman Gupta
                    new EmployeeSkill(11, 9, 4),
                    new EmployeeSkill(12, 1, 5), // Riya Sen
                    new EmployeeSkill(12, 17, 4),
                    new EmployeeSkill(13, 2, 5), // Siddharth Jain
                    new EmployeeSkill(13, 25, 4),
                    new EmployeeSkill(13, 28, 3),
                    new EmployeeSkill(14, 34, 4), // Megha Kapoor - DevOps
                    new EmployeeSkill(14, 35, 4),
                    new EmployeeSkill(15, 53, 4), // QA
                    new EmployeeSkill(15, 55, 3),
                    new EmployeeSkill(16, 41, 4), // Mobile
                    new EmployeeSkill(16, 7, 3),
                    new EmployeeSkill(17, 50, 5), // Security
                    new EmployeeSkill(17, 52, 4),
                    new EmployeeSkill(18, 1, 3), // Full Stack
                    new EmployeeSkill(18, 3, 3),
                    new EmployeeSkill(18, 17, 3),
                    new EmployeeSkill(18, 9, 3),

                    // ================= TEAM 3 =================
                    new EmployeeSkill(20, 2, 5), // AI
                    new EmployeeSkill(20, 25, 4),
                    new EmployeeSkill(21, 2, 5), // Data Scientist
                    new EmployeeSkill(21, 30, 4), // Data Analysis
                    new EmployeeSkill(21, 31, 3), // Pandas
                    new EmployeeSkill(22, 1, 5), // Backend
                    new EmployeeSkill(22, 17, 4),
                    new EmployeeSkill(23, 3, 5), // Frontend
                    new EmployeeSkill(23, 9, 4),
                    new EmployeeSkill(24, 34, 4), // DevOps
                    new EmployeeSkill(24, 36, 3), // Kubernetes
                    new EmployeeSkill(25, 53, 4), // QA
                    new EmployeeSkill(25, 54, 4),
                    new EmployeeSkill(26, 41, 4), // Mobile
                    new EmployeeSkill(26, 42, 3),
                    new EmployeeSkill(27, 1, 4), // Full Stack
                    new EmployeeSkill(27, 17, 3),
                    new EmployeeSkill(27, 9, 3),

                    // ================= TEAM 4 =================
                    new EmployeeSkill(29, 34, 5), // DevOps
                    new EmployeeSkill(29, 35, 4),
                    new EmployeeSkill(30, 1, 5), // Backend
                    new EmployeeSkill(30, 17, 4),
                    new EmployeeSkill(31, 3, 5), // Frontend
                    new EmployeeSkill(31, 9, 4),
                    new EmployeeSkill(32, 2, 5), // AI
                    new EmployeeSkill(32, 25, 4),
                    new EmployeeSkill(33, 53, 4), // QA
                    new EmployeeSkill(33, 54, 3),
                    new EmployeeSkill(34, 41, 4), // Mobile
                    new EmployeeSkill(35, 50, 5), // Security
                    new EmployeeSkill(36, 1, 4), // Full Stack
                    new EmployeeSkill(36, 17, 3),

                    // ================= TEAM 5 =================
                    new EmployeeSkill(38, 42, 5), // Mobile
                    new EmployeeSkill(39, 3, 5), // Frontend
                    new EmployeeSkill(39, 9, 4),
                    new EmployeeSkill(40, 1, 5), // Backend
                    new EmployeeSkill(40, 17, 4),
                    new EmployeeSkill(41, 2, 5), // AI
                    new EmployeeSkill(41, 25, 4),
                    new EmployeeSkill(42, 34, 4), // DevOps
                    new EmployeeSkill(42, 36, 3),
                    new EmployeeSkill(43, 53, 4), // QA
                    new EmployeeSkill(44, 50, 5), // Security
                    new EmployeeSkill(45, 1, 4), // Full Stack

                    // ================= TEAM 6 =================
                    new EmployeeSkill(47, 50, 5), // Security
                    new EmployeeSkill(48, 1, 5), // Backend
                    new EmployeeSkill(48, 17, 4),
                    new EmployeeSkill(49, 3, 5), // Frontend
                    new EmployeeSkill(49, 9, 4),
                    new EmployeeSkill(50, 2, 5), // AI
                    new EmployeeSkill(50, 25, 4),
                    new EmployeeSkill(51, 34, 4), // DevOps
                    new EmployeeSkill(52, 53, 4), // QA
                    new EmployeeSkill(53, 42, 4), // Mobile
                    new EmployeeSkill(54, 1, 4), // Full Stack

                    // ================= TEAM 7 =================
                    new EmployeeSkill(56, 53, 5), // QA
                    new EmployeeSkill(57, 1, 5), // Backend
                    new EmployeeSkill(58, 3, 5), // Frontend
                    new EmployeeSkill(59, 2, 5), // AI
                    new EmployeeSkill(60, 34, 4), // DevOps
                    new EmployeeSkill(61, 42, 4), // Mobile
                    new EmployeeSkill(62, 50, 5), // Security
                    new EmployeeSkill(63, 1, 4), // Full Stack

                    // ================= TEAM 8 =================
                    new EmployeeSkill(65, 1, 5), // Full Stack
                    new EmployeeSkill(65, 3, 4),
                    new EmployeeSkill(66, 3, 5), // Frontend
                    new EmployeeSkill(66, 9, 4),
                    new EmployeeSkill(67, 1, 5), // Backend
                    new EmployeeSkill(67, 17, 4),
                    new EmployeeSkill(68, 2, 5), // AI
                    new EmployeeSkill(68, 25, 4),
                    new EmployeeSkill(69, 34, 4), // DevOps
                    new EmployeeSkill(70, 53, 4), // QA
                    new EmployeeSkill(71, 42, 4), // Mobile
                    new EmployeeSkill(72, 50, 5) // Security
            );

            for(EmployeeSkill employeeSkill: employeeSkills)
                employeeSkillService.assignSkillToEmployee(employeeSkill.getEmp_id(), employeeSkill.getSkill_id(), employeeSkill.getProficiency());
            break;
            case 2: System.out.print("Enter Employee Id: ");
                int empId = sc.nextInt();
                System.out.print("Enter Skill Id: ");
                int skillId = sc.nextInt();

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
