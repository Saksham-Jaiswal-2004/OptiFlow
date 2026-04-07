package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.ProjectSkill;
import com.optiflow.models.User;
import com.optiflow.services.ProjectSkillService;
import com.optiflow.utils.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class ProjectSkillTest
{
    public static void main(String[] args) throws SQLException
    {
        ProjectSkillService projectSkillService = new ProjectSkillService();

        List<ProjectSkill> projectSkills = List.of(
                // Project 1 - E-Commerce Platform
                new ProjectSkill(17, 1), // Java
                new ProjectSkill(17, 17), // Spring Boot
                new ProjectSkill(17, 3), // JavaScript
                new ProjectSkill(17, 9), // React.js
                new ProjectSkill(17, 10), // Next.js
                new ProjectSkill(17, 11), // HTML
                new ProjectSkill(17, 12), // CSS
                new ProjectSkill(17, 22), // REST APIs
                new ProjectSkill(17, 48), // Firebase

                // Project 2 - AI Chatbot
                new ProjectSkill(18, 2), // Python
                new ProjectSkill(18, 25), // Machine Learning
                new ProjectSkill(18, 27), // NLP
                new ProjectSkill(18, 28), // TensorFlow
                new ProjectSkill(18, 29), // PyTorch
                new ProjectSkill(18, 30), // Data Analysis
                new ProjectSkill(18, 31), // Pandas
                new ProjectSkill(18, 32), // NumPy
                new ProjectSkill(18, 33), // Data Engineering

                // Project 3 - Cloud Infrastructure Setup
                new ProjectSkill(19, 34), // AWS
                new ProjectSkill(19, 35), // Docker
                new ProjectSkill(19, 36), // Kubernetes
                new ProjectSkill(19, 37), // CI/CD
                new ProjectSkill(19, 38), // Terraform
                new ProjectSkill(19, 39), // Linux
                new ProjectSkill(19, 40), // Nginx
                new ProjectSkill(19, 22), // REST APIs
                new ProjectSkill(19, 24)  // Microservices
        );

        UserDAO userDAO = new UserDAO();
        User u = userDAO.getUserById(11);
        SessionManager.setUser(u);
//        System.out.println(u.getName());

        for(ProjectSkill projectSkill: projectSkills)
            projectSkillService.addSkillToProject(u, projectSkill.getProjectId(), projectSkill.getSkillId());
    }
}
