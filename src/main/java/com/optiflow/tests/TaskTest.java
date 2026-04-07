package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.Tasks;
import com.optiflow.models.User;
import com.optiflow.services.TaskService;
import com.optiflow.utils.SessionManager;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TaskTest
{
    public static void main(String[] args) throws SQLException
    {
        TaskService taskService = new TaskService();
        UserDAO userDAO = new UserDAO();
        User u = userDAO.getUserById(11);

        SessionManager.setUser(u);

        List<Tasks> tasks = List.of(
                // ================= Project 1 Tasks =================
                new Tasks(17, 21, "Backend API Development", "Develop REST APIs for products, orders, and users", "IN_PROGRESS", "HIGH", 80, 20, Date.valueOf("2026-04-01"), Date.valueOf("2026-05-15")),
                new Tasks(17, 22, "Frontend UI Development", "Build responsive UI for desktop and mobile using React.js", "IN_PROGRESS", "HIGH", 70, 15, Date.valueOf("2026-04-05"), Date.valueOf("2026-05-20")),
                new Tasks(17, 28, "Database Integration", "Integrate Firebase for real-time database operations", "PENDING", "MEDIUM", 50, 0, Date.valueOf("2026-04-10"), Date.valueOf("2026-05-30")),
                new Tasks(17, 23, "AI Recommendation Module", "Add AI-based product recommendation system", "PENDING", "MEDIUM", 60, 0, Date.valueOf("2026-05-01"), Date.valueOf("2026-06-10")),

                // ================= Project 2 Tasks =================
                new Tasks(18, 31, "Chatbot Model Training", "Train NLP model using customer queries", "IN_PROGRESS", "HIGH", 90, 30, Date.valueOf("2026-04-05"), Date.valueOf("2026-06-01")),
                new Tasks(18, 33, "Data Processing Pipeline", "Build pipeline for query preprocessing and analysis", "IN_PROGRESS", "MEDIUM", 60, 20, Date.valueOf("2026-04-10"), Date.valueOf("2026-06-10")),
                new Tasks(18, 30, "API Integration", "Integrate chatbot backend with frontend app", "PENDING", "MEDIUM", 50, 0, Date.valueOf("2026-05-01"), Date.valueOf("2026-06-20"))
        );

        for(Tasks task: tasks)
            taskService.createTask(task);
    }
}
