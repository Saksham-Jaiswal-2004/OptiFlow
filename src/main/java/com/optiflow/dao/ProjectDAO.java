package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Projects;
import com.optiflow.models.Tasks;
import com.optiflow.services.AuditLogService;
import com.optiflow.utils.DBUtility;
import com.optiflow.utils.SessionManager;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ProjectDAO
{
    private AuditLogService auditLogService;
    private DBUtility dbUtility;

    public  ProjectDAO()
    {
        this.auditLogService = new AuditLogService();
        this.dbUtility = new DBUtility();
    }

    public boolean createProject(String name, String description, Date start_date, Date deadline, int manager_id, String status) throws SQLException
    {
        String sql = "INSERT INTO projects (name, description, start_date, end_date, deadline, manager_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDate(3, Date.valueOf(start_date.toLocalDate()));
            stmt.setDate(4, null);
            stmt.setDate(5, Date.valueOf(deadline.toLocalDate()));
            if(manager_id == 0)
                stmt.setNull(6, java.sql.Types.INTEGER);
            else
                stmt.setInt(6, manager_id);
            stmt.setString(7, status);

            int rows = stmt.executeUpdate();

            if (rows > 0)
            {
                ResultSet rs = stmt.getGeneratedKeys();

                if (rs.next())
                {
                    int generatedId = rs.getInt(1);
                    auditLogService.logAction(SessionManager.getUser().getUserId(), "ADD_PROJECT", "PROJECT", generatedId, SessionManager.getUser().getName()+" added a new project");
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
                pro.setEnd_date(rs.getDate("deadline"));
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
                pro.setEnd_date(rs.getDate("deadline"));
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
                pro.setEnd_date(rs.getDate("deadline"));
                pro.setManager_id(rs.getInt("manager_id"));
                pro.setStatus(rs.getString("status"));

                proList.add(pro);
            }
        }
        return proList;
    }

    public double getProjectCompletionRate(int managerId) throws SQLException
    {
        String totalQuery = "SELECT COUNT(*) FROM projects WHERE manager_id = ?";
        String completedQuery = "SELECT COUNT(*) FROM projects WHERE manager_id = ? AND status = 'COMPLETED'";

        int total = dbUtility.getCount(totalQuery, managerId);
        int completed = dbUtility.getCount(completedQuery, managerId);

        if (total == 0) return 0;

        return (double) completed / total;
    }

    public double getOnTimeDeliveryRate(int managerId) throws SQLException
    {
        String query = """
        SELECT COUNT(*) 
        FROM projects 
        WHERE manager_id = ? 
        AND status = 'COMPLETED'
        AND actual_end_date <= deadline
    """;

        String totalCompletedQuery = """
        SELECT COUNT(*) 
        FROM projects 
        WHERE manager_id = ? 
        AND status = 'COMPLETED'
    """;

        int onTime = dbUtility.getCount(query, managerId);
        int totalCompleted = dbUtility.getCount(totalCompletedQuery, managerId);

        if (totalCompleted == 0) return 0;

        return (double) onTime / totalCompleted;
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
                pro.setEnd_date(rs.getDate("deadline"));
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

//    private double getProjectCompletionRate(int managerId) throws SQLException
//    {
//        String totalQuery = "SELECT COUNT(*) FROM projects WHERE manager_id = ?";
//        String completedQuery = "SELECT COUNT(*) FROM projects WHERE manager_id = ? AND status = 'COMPLETED'";
//
//        int total = getCount(totalQuery, managerId);
//        int completed = getCount(completedQuery, managerId);
//
//        if (total == 0) return 0;
//
//        return (double) completed / total;
//    }

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

    public int updateDeadline(int project_id, Date deadline) throws SQLException
    {
        String sql = "UPDATE projects SET deadline=? WHERE project_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setDate(1, Date.valueOf(deadline.toLocalDate()));
            stmt.setInt(2, project_id);

            rs = stmt.executeUpdate();
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

    public static void main(String[] args) throws SQLException
    {
        ProjectDAO projectDAO = new ProjectDAO();
        Date date = java.sql.Date.valueOf(java.time.LocalDate.now());
        projectDAO.createProject("Project 1", "Project Desc 1", date, date, 8, "IN_PROGRESS");
        projectDAO.createProject("Project 2", "Project Desc 2", date, date, 8, "IN_PROGRESS");
        projectDAO.createProject("Project 3", "Project Desc 3", date, date, 8, "IN_PROGRESS");
        projectDAO.createProject("Project 4", "Project Desc 4", date, date, 8, "IN_PROGRESS");
    }
}
