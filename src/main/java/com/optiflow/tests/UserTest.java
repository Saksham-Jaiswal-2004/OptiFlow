package com.optiflow.tests;

import java.util.*;
import com.optiflow.dao.UserDAO;
import com.optiflow.database.DBConnection;
import com.optiflow.models.User;

import java.sql.*;

public class UserTest
{
    public static void main(String[] args) throws SQLException {
        Connection conn = DBConnection.getConnection();
        Scanner sc = new Scanner(System.in);
        UserDAO testUser = new UserDAO();

//        System.out.println("********    User Test    ********");
//        System.out.print("Enter Name: ");
//        String name = sc.nextLine();
//        System.out.print("Enter Email: ");
//        String email = sc.nextLine();
//        System.out.print("Enter Password: ");
//        String pass = sc.nextLine();
//        System.out.print("Enter Role: ");
//        String role = sc.nextLine();
//
//        try {
//            testUser.addUser(name, email, pass, role);
//            System.out.println("User Added Successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        System.out.print("Enter Email to check: ");
//        String emailCheck = sc.next();
//        User resUser = testUser.getUserByEmail(emailCheck);
//        System.out.println("User: "+resUser.getName());

//        System.out.println("Getting All Users: ");
//        List<User> userList = testUser.getAllUsers();
//        for(User test: userList)
//        {
//            System.out.println(test.getName());
//        }

//        System.out.print("Enter User Id to update: ");
//        int uid = sc.nextInt();
//        System.out.print("Enter new name: ");
//        String name = sc.next();
//        testUser.updateName(uid, name);

//        System.out.print("Enter User Id to update: ");
//        int uid = sc.nextInt();
//        System.out.print("Enter new email: ");
//        String email = sc.next();
//        testUser.updateEmail(uid, email);

        System.out.print("Enter User Id to delete: ");
        int user_id = sc.nextInt();
        testUser.deleteUser(user_id);
    }
}
