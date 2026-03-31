package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ProjectDAO
{
    public boolean createProject(String name, String description, Date start_date, Date end_date, int client_id, String status) throws SQLException
    {
        String sql = "INSERT INTO projects (name, description, start_date, end_date, manager_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDate(3, Date.valueOf(start_date.toLocalDate()));
            stmt.setDate(4, Date.valueOf(end_date.toLocalDate()));
            if(client_id == 0)
                stmt.setNull(5, java.sql.Types.INTEGER);
            else
                stmt.setInt(5, client_id);
            stmt.setString(6, status);
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Projects getProjectById(int projectId) throws SQLException
    {
        String sql = "SELECT * FROM projects WHERE project_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Projects pro = new Projects();
                pro.setProject_id(rs.getInt("project_id"));
                pro.setName(rs.getString("name"));
                pro.setDescription(rs.getString("description"));
                pro.setStart_date(rs.getDate("start_date"));
                pro.setEnd_date(rs.getDate("end_date"));
                pro.setManager_id(rs.getInt("manager_id"));
                pro.setStatus(rs.getString("status"));
                return pro;
            }
        }

        return null;
    }

    public List<Projects> getAllProjects() throws SQLException
    {
        LinkedList<Projects> proList = new LinkedList<>();
        String sql = "SELECT * FROM projects";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Projects pro = new Projects();
                pro.setProject_id(rs.getInt("project_id"));
                pro.setName(rs.getString("name"));
                pro.setDescription(rs.getString("description"));
                pro.setStart_date(rs.getDate("start_date"));
                pro.setEnd_date(rs.getDate("end_date"));
                pro.setManager_id(rs.getInt("manager_id"));
                pro.setStatus(rs.getString("status"));

                proList.add(pro);
            }
        }
        return proList;
    }

    public List<Projects> getProjectsByStatus(String status) throws SQLException
    {
        LinkedList<Projects> proList = new LinkedList<>();
        String sql = "SELECT * FROM projects WHERE status=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Projects pro = new Projects();
                pro.setProject_id(rs.getInt("project_id"));
                pro.setName(rs.getString("name"));
                pro.setDescription(rs.getString("description"));
                pro.setStart_date(rs.getDate("start_date"));
                pro.setEnd_date(rs.getDate("end_date"));
                pro.setManager_id(rs.getInt("manager_id"));
                pro.setStatus(rs.getString("status"));

                proList.add(pro);
            }
        }
        return proList;
    }

    public List<Projects> getProjectsByManager(int manager_id) throws SQLException
    {
        LinkedList<Projects> proList = new LinkedList<>();
        String sql = "SELECT * FROM projects WHERE manager_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, manager_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Projects pro = new Projects();
                pro.setProject_id(rs.getInt("project_id"));
                pro.setName(rs.getString("name"));
                pro.setDescription(rs.getString("description"));
                pro.setStart_date(rs.getDate("start_date"));
                pro.setEnd_date(rs.getDate("end_date"));
                pro.setManager_id(rs.getInt("manager_id"));
                pro.setStatus(rs.getString("status"));

                proList.add(pro);
            }
        }
        return proList;
    }

    public String getProjectStatus(int project_id) throws SQLException
    {
        String sql = "SELECT * FROM projects WHERE project_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, project_id);
            ResultSet rs = stmt.executeQuery();

            return rs.getString("status");
        }
    }

    public int updateName(int project_id, String name) throws SQLException
    {
        String sql = "UPDATE projects SET name=? WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, name);
            stmt.setInt(2, project_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateDescription(int project_id, String description) throws SQLException
    {
        String sql = "UPDATE projects SET description=? WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, description);
            stmt.setInt(2, project_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateStartDate(int project_id, Date start_date) throws SQLException
    {
        String sql = "UPDATE projects SET start_date=? WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setDate(1, Date.valueOf(start_date.toLocalDate()));
            stmt.setInt(2, project_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateEndDate(int project_id, Date end_date) throws SQLException
    {
        String sql = "UPDATE projects SET end_date=? WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setDate(1, Date.valueOf(end_date.toLocalDate()));
            stmt.setInt(2, project_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateStatus(int project_id, String status) throws SQLException
    {
        String sql = "UPDATE projects SET status=? WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, status);
            stmt.setInt(2, project_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int deleteProject(int project_id) throws SQLException
    {
        String sql = "DELETE FROM projects WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, project_id);

            rs = stmt.executeUpdate();
        }

        return rs;
    }
    public List<Tasks> getCompletedTasks(int project_id) throws SQLException
    {
        LinkedList<Tasks> taskList = new LinkedList<>();
        String sql = "SELECT * FROM tasks WHERE (project_id, status) VALUES (?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, project_id);
            stmt.setString(2, "Completed");
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
