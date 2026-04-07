package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.TaskSkill;
import com.optiflow.models.User;
import com.optiflow.services.TaskSkillService;

import java.sql.SQLException;
import java.util.List;

public class TaskSkillTest
{
    public static void main(String[] args) throws SQLException {
        TaskSkillService taskSkillService = new TaskSkillService();

        List<TaskSkill> taskSkills = List.of(
                // Project 1 - Task Skills
                new TaskSkill(45,1), // Java
                new TaskSkill(45,17), // Spring Boot
                new TaskSkill(45,22), // REST APIs

                new TaskSkill(46,3), // JS
                new TaskSkill(46,9), // React
                new TaskSkill(46,10), // Next.js
                new TaskSkill(46,11), // HTML
                new TaskSkill(46,12), // CSS
                new TaskSkill(46,13), // Tailwind CSS

                new TaskSkill(47,48), // Firebase
                new TaskSkill(47,22), // REST APIs

                new TaskSkill(48,2), // Python
                new TaskSkill(48,25), // ML

                // Project 2 - Task Skills
                new TaskSkill(49,2), // Python
                new TaskSkill(49,25), // ML
                new TaskSkill(49,27), // NLP
                new TaskSkill(49,28), // TensorFlow
                new TaskSkill(49,29), // PyTorch

                new TaskSkill(50,30), // Data Analysis
                new TaskSkill(50,31), // Pandas
                new TaskSkill(50,32), // NumPy
                new TaskSkill(50,33), // Data Engineering

                new TaskSkill(51,22), // REST APIs
                new TaskSkill(51,23) // GraphQL
        );

        UserDAO userDAO = new UserDAO();
        User u = userDAO.getUserById(11);

        for(TaskSkill ts: taskSkills)
            taskSkillService.addSkillToTask(u, ts.getTaskId(), ts.getSkillId());
    }
}
