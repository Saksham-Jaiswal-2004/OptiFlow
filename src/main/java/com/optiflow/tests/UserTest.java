package com.optiflow.tests;

import java.util.*;
import com.optiflow.dao.UserDAO;
import com.optiflow.database.DBConnection;
import java.sql.*;

public class UserTest
{
    public static void main(String[] args)
    {
        Connection conn = DBConnection.getConnection();
        Scanner sc = new Scanner(System.in);
        UserDAO testUser = new UserDAO();

        System.out.println("********    User Test    ********");
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        System.out.print("Enter Role: ");
        String role = sc.nextLine();

        try {
            testUser.addUser(name, email, pass, role);
            System.out.println("User Added Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
