package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.ProjectSkill;
import com.optiflow.models.Tasks;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class TaskDAO
{
    public boolean createTask(int project_id, int assigned_to, String title, String description, String status, String priority, int estimated_hours, Date start_date, Date end_date) throws SQLException
    {
        String sql = "INSERT INTO tasks (project_id, assigned_to, title, description, status, priority, estimated_hours, actual_hours, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, project_id);
            if(assigned_to == 0)
                stmt.setNull(2, java.sql.Types.INTEGER);
            else
                stmt.setInt(2, assigned_to);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, status);
            stmt.setString(6, priority);
            stmt.setInt(7, estimated_hours);
            stmt.setInt(8, 0);
            stmt.setDate(9, Date.valueOf(start_date.toLocalDate()));
            stmt.setDate(10, Date.valueOf(end_date.toLocalDate()));
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Tasks> getAllTasks() throws SQLException
    {
        List<Tasks> tasksList = new LinkedList<>();
        String sql = "SELECT * FROM tasks";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                tasksList.add(task);
            }

                return tasksList;
        } catch (Exception e) {
            return null;
        }
    }

    public Tasks getTaskById(int taskId) throws SQLException
    {
        String sql = "SELECT * FROM tasks WHERE task_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));
                return task;
            }
        }

        return null;
    }

    public List<Tasks> getTasksByProject(int project_id) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE project_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, project_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                taskList.add(task);
            }
        }
        return taskList;
    }

    public List<Tasks> getTasksByEmployee(int employee_id) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE assigned_to=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, employee_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                taskList.add(task);
            }
        }
        return taskList;
    }

    public List<Tasks> getTasksByStatus(String status) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE status=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                taskList.add(task);
            }
        }
        return taskList;
    }

    public List<Tasks> getTasksByPriority(String priority) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE priority=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, priority);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                taskList.add(task);
            }
        }
        return taskList;
    }

    public int assignTask(int task_id, int emp_id) throws SQLException
    {
        String sql = "UPDATE tasks SET assigned_to=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, emp_id);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateTitle(int task_id, String title) throws SQLException
    {
        String sql = "UPDATE tasks SET title=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, title);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateDescription(int task_id, String description) throws SQLException
    {
        String sql = "UPDATE tasks SET description=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, description);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateStatus(int task_id, String status) throws SQLException
    {
        String sql = "UPDATE tasks SET status=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, status);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updatePriority(int task_id, String priority) throws SQLException
    {
        String sql = "UPDATE tasks SET priority=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, priority);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateEstimatedHours(int task_id, int estimated_hours) throws SQLException
    {
        String sql = "UPDATE tasks SET estimated_hours=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, estimated_hours);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateActualHours(int task_id, int actual_hours) throws SQLException
    {
        String sql = "UPDATE tasks SET actual_hours=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, actual_hours);
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateStartDate(int task_id, @NotNull Date start_date) throws SQLException
    {
        String sql = "UPDATE tasks SET start_date=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setDate(1, Date.valueOf(start_date.toLocalDate()));
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateEndDate(int task_id, @NotNull Date end_date) throws SQLException
    {
        String sql = "UPDATE tasks SET end_date=? WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setDate(1, Date.valueOf(end_date.toLocalDate()));
            stmt.setInt(2, task_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int deleteTask(int task_id) throws SQLException
    {
        String sql = "DELETE FROM tasks WHERE task_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, task_id);

            rs = stmt.executeUpdate();
        }

        return rs;
    }

    public int getTotalEstimatedHoursByEmployee(int emp_id) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE assigned_to=?";
        int total=0;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, emp_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                total += task.getEstimated_hours();
            }
        }
        return total;
    }

    public int getTotalActualHoursByEmployee(int emp_id) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE assigned_to=?";
        int total=0;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, emp_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                total += task.getActual_hours();
            }
        }
        return total;
    }

    public List<Tasks> getDelayedTasks() throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                if(LocalDate.now().isAfter(task.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                    taskList.add(task);
            }
        }
        return taskList;
    }

    public List<Tasks> getTasksDueSoon() throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                if(LocalDate.now().isEqual(task.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
                    taskList.add(task);
            }
        }
        return taskList;
    }

    public List<Tasks> getHighPriorityTasks() throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE priority=high";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Tasks task = new Tasks();
                task.setTask_id(rs.getInt("task_id"));
                task.setProject_id(rs.getInt("project_id"));
                task.setAssigned_to(rs.getInt("assigned_to"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setStatus(rs.getString("status"));
                task.setPriority(rs.getString("priority"));
                task.setEstimated_hours(rs.getInt("estimated_hours"));
                task.setActual_hours(rs.getInt("actual_hours"));
                task.setStart_date(rs.getDate("start_date"));
                task.setEnd_date(rs.getDate("end_date"));

                taskList.add(task);
            }
        }
        return taskList;
    }
}
