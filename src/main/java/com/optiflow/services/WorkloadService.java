package com.optiflow.services;

import com.optiflow.dao.EmployeeDAO;
import com.optiflow.dao.TaskDAO;
import com.optiflow.models.Tasks;

import java.sql.SQLException;
import java.util.List;

public class WorkloadService
{
    private TaskDAO taskDAO;
    private EmployeeDAO employeeDAO;

    WorkloadService()
    {
        this.taskDAO = new TaskDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    public int calculateCurrentWorkload(int emp_id) throws SQLException
    {
        if(emp_id <= 0)
            return 0;

        List<Tasks> tasks = taskDAO.getTasksByEmployee(emp_id);

        int total = 0;

        for(Tasks t : tasks)
        {
            if(!t.getStatus().equalsIgnoreCase("COMPLETED"))
            {
                total += t.getEstimated_hours();
            }
        }

        return total;
    }

    public int getAvailableCapacity(int emp_id) throws SQLException
    {
        if(emp_id <= 0)
            return 0;

        int capacity = employeeDAO.getWeeklyCapacity(emp_id);
        int used = calculateCurrentWorkload(emp_id);

        return capacity - used;
    }

    public boolean isEmployeeOverloaded(int emp_id) throws SQLException
    {
        return getAvailableCapacity(emp_id) < 0;
    }

    public boolean canAssignTask(int emp_id, int taskHours) throws SQLException
    {
        if(emp_id <= 0)
            return false;

        return getAvailableCapacity(emp_id) >= taskHours;
    }

    public int getBestEmployee(List<Integer> empIds, int requiredHours) throws SQLException
    {
        int bestEmpId = -1;
        int minLoad = Integer.MAX_VALUE;

        for(Integer empId : empIds)
        {
            if(!canAssignTask(empId, requiredHours))
                continue;

            int load = calculateCurrentWorkload(empId);

            if(load < minLoad)
            {
                minLoad = load;
                bestEmpId = empId;
            }
        }

        return bestEmpId;
    }
}
