package com.optiflow.tests;

import java.util.*;
import com.optiflow.dao.EmployeeDAO;
import com.optiflow.database.DBConnection;
import java.sql.*;

public class EmployeeTest
{
    public static void main(String[] args)
    {
        Connection conn = DBConnection.getConnection();
        Scanner sc = new Scanner(System.in);
        EmployeeDAO testEmployee = new EmployeeDAO();

        System.out.println("********    Employee Test    ********");
        System.out.print("Enter User ID: ");
        int user_id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Skill: ");
        String skill = sc.nextLine();
        System.out.print("Enter Designation: ");
        String designation = sc.nextLine();
        System.out.print("Enter Department: ");
        String department = sc.nextLine();
        System.out.print("Enter Manager ID: ");
        int manager_id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Status: ");
        String status = sc.nextLine();
        System.out.print("Enter Weekly Capacity: ");
        int weekly_capacity = sc.nextInt();
        sc.nextLine();

        try {
            testEmployee.addEmployee(user_id, name, skill, designation, department, manager_id, status, weekly_capacity);
            System.out.println("Employee Added Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
