package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Tasks;
import com.optiflow.models.WorkLog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class WorkLogDAO
{
    private TaskDAO taskDAO;

    public WorkLogDAO()
    {
        this.taskDAO = new TaskDAO();
    }

    public boolean addWorkLog(int empId, int taskId, Date workDate, int hours, String desc)
    {
        String sql = "INSERT INTO worklog (employee_id, task_to, work_date, hours_worked, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            stmt.setInt(2, taskId);
            stmt.setDate(3, workDate);
            stmt.setInt(4, hours);
            stmt.setString(5, desc);
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WorkLog> getLogsByEmployee(int empId)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        String sql = "SELECT * FROM worklog WHERE employee_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                WorkLog workLog = new WorkLog();
                workLog.setLogId(rs.getInt("log_id"));
                workLog.setEmployeeId(rs.getInt("employee_id"));
                workLog.setTaskId(rs.getInt("task_id"));
                workLog.setWorkDate(rs.getDate("work_date"));
                workLog.setHoursWorked(rs.getInt("hours"));
                workLog.setDescription(rs.getString("description"));

                workLogs.add(workLog);
            }

            return workLogs;
        } catch (Exception e) {
            return null;
        }
    }

    public List<WorkLog> getLogsByEmployeeByDate(int empId, Date date)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        String sql = "SELECT * FROM worklog WHERE (employee_id, work_date) VALUES (?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            stmt.setDate(2, date);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                WorkLog workLog = new WorkLog();
                workLog.setLogId(rs.getInt("log_id"));
                workLog.setEmployeeId(rs.getInt("employee_id"));
                workLog.setTaskId(rs.getInt("task_id"));
                workLog.setWorkDate(rs.getDate("work_date"));
                workLog.setHoursWorked(rs.getInt("hours"));
                workLog.setDescription(rs.getString("description"));

                workLogs.add(workLog);
            }

            return workLogs;
        } catch (Exception e) {
            return null;
        }
    }

    public List<WorkLog> getLogsByTask(int taskId)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        String sql = "SELECT * FROM worklog WHERE task_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                WorkLog workLog = new WorkLog();
                workLog.setLogId(rs.getInt("log_id"));
                workLog.setEmployeeId(rs.getInt("employee_id"));
                workLog.setTaskId(rs.getInt("task_id"));
                workLog.setWorkDate(rs.getDate("work_date"));
                workLog.setHoursWorked(rs.getInt("hours"));
                workLog.setDescription(rs.getString("description"));

                workLogs.add(workLog);
            }

            return workLogs;
        } catch (Exception e) {
            return null;
        }
    }

    public List<WorkLog> getLogsByProject(int projectId)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        String sql = "SELECT * FROM worklog";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                WorkLog workLog = new WorkLog();
                workLog.setLogId(rs.getInt("log_id"));
                workLog.setEmployeeId(rs.getInt("employee_id"));
                workLog.setTaskId(rs.getInt("task_id"));
                workLog.setWorkDate(rs.getDate("work_date"));
                workLog.setHoursWorked(rs.getInt("hours"));
                workLog.setDescription(rs.getString("description"));

                if(taskDAO.getTaskById(workLog.getTaskId()).getProject_id() == projectId)
                    workLogs.add(workLog);
            }

            return workLogs;
        } catch (Exception e) {
            return null;
        }
    }

//    public List<WorkLog> getLogsByDateRange(Date start, Date end)
//    {
//        List<WorkLog> workLogs = new LinkedList<>();
//        return workLogs;
//    }

    public int getTotalHoursByEmployee(int empId)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        int total=0;
        String sql = "SELECT * FROM worklog WHERE employee_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                WorkLog workLog = new WorkLog();
                workLog.setLogId(rs.getInt("log_id"));
                workLog.setEmployeeId(rs.getInt("employee_id"));
                workLog.setTaskId(rs.getInt("task_id"));
                workLog.setWorkDate(rs.getDate("work_date"));
                workLog.setHoursWorked(rs.getInt("hours"));
                workLog.setDescription(rs.getString("description"));

                workLogs.add(workLog);

                total += workLog.getHoursWorked();
            }

            return total;
        } catch (Exception e) {
            return -1;
        }
    }
}
