package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.Projects;
import com.optiflow.services.ProjectService;
import com.optiflow.utils.SessionManager;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ProjectTest
{
    public static void main(String[] args) throws SQLException
    {
        ProjectService projectService = new ProjectService();
        UserDAO userDAO = new UserDAO();

        SessionManager.setUser(userDAO.getUserById(11));

        List<Projects> projects = List.of(
                new Projects("E-Commerce Platform", "Full-fledged e-commerce web application with AI recommendations.",
                        Date.valueOf("2026-04-01"), Date.valueOf("2026-07-01"), 1, "IN_PROGRESS"),

                new Projects("AI Chatbot", "Smart AI-powered customer support chatbot integrated with NLP and ML.",
                        Date.valueOf("2026-04-05"), Date.valueOf("2026-07-10"), 10, "IN_PROGRESS"),

                new Projects("Cloud Infrastructure Setup", "Deploy and manage scalable cloud infrastructure with CI/CD pipelines.",
                        Date.valueOf("2026-04-10"), Date.valueOf("2026-07-15"), 19, "IN_PROGRESS")
        );

        for(Projects project: projects)
            projectService.createProject(project);
    }
}
