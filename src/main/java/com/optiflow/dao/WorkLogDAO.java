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
    public boolean addWorkLog(int empId, int taskId, int hours, String desc)
    {
        String sql = "INSERT INTO worklogs (employee_id, task_to, hours, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            stmt.setInt(2, taskId);
            stmt.setInt(3, hours);
            stmt.setString(4, desc);
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WorkLog> getLogsByEmployee(int empId)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        String sql = "SELECT * FROM worklogs WHERE employee_id=?";

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
        String sql = "SELECT * FROM worklogs WHERE (employee_id, work_date) VALUES (?, ?)";

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
        String sql = "SELECT * FROM worklogs WHERE task_id=?";

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

//    public List<WorkLog> getLogsByDateRange(Date start, Date end)
//    {
//        List<WorkLog> workLogs = new LinkedList<>();
//        return workLogs;
//    }

    public int getTotalHoursByEmployee(int empId)
    {
        List<WorkLog> workLogs = new LinkedList<>();
        int total=0;
        String sql = "SELECT * FROM worklogs WHERE employee_id=?";

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
