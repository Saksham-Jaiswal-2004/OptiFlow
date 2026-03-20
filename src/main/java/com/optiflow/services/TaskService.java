package com.optiflow.services;

import com.optiflow.models.Tasks;
import java.util.LinkedList;
import java.util.List;

public class TaskService
{
    public boolean createTask(Tasks task)
    {
        return true;
    }

    public Tasks getTaskById(int taskId)
    {
        Tasks t1 = new Tasks();

        return t1;
    }

    public List<Tasks> getAllTasks()
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }

    public boolean updateTask(Tasks task)
    {
        return true;
    }

    public boolean deleteTask(int taskId)
    {
        return true;
    }

    public boolean assignTask(int taskId, int empId)
    {
        return true;
    }

    public boolean autoAssignTask(Tasks task)
    {
        return true;
    }

    public boolean updateTaskStatus(int taskId, String status)
    {
        return true;
    }

    public List<Tasks> getTasksByStatus(String status)
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }

    public List<Tasks> getTasksByEmployee(int empId)
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }

    public int calculateTaskHours(int taskId)
    {
        return -1;
    }

    public boolean canAssignTask(int empId, int taskHours)
    {
        return true;
    }

    public List<Tasks> getOverdueTasks()
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }

    public List<Tasks> getTasksDueSoon()
    {
        LinkedList<Tasks> taskList = new LinkedList<>();

        return taskList;
    }
}
