package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.Employee;
import com.optiflow.services.EmployeeService;
import com.optiflow.utils.SessionManager;

import java.util.*;

import java.sql.*;

public class EmployeeTest
{
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        EmployeeService employeeService = new EmployeeService();

        System.out.println("********    Auth Test    ********");
        System.out.println("1. Add Employees");
        System.out.print("Enter your choice: ");
        int ch = sc.nextInt();
        sc.nextLine();

        switch (ch) {
            case 1:
                UserDAO userDAO = new UserDAO();
                SessionManager.setUser(userDAO.getUserById(11));
                List<Employee> employees = List.of(

                        // ================= TEAM 1 =================
                        new Employee(13,"Amit Sharma","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(21,"Rohit Verma","Backend Developer","Engineering",13,"ACTIVE",40),
                        new Employee(22,"Karan Mehta","Frontend Developer","Engineering",13,"ACTIVE",40),
                        new Employee(23,"Priya Shah","AI/ML Engineer","AI",13,"ACTIVE",40),
                        new Employee(24,"Arjun Das","DevOps Engineer","Cloud",13,"ACTIVE",40),
                        new Employee(25,"Neeraj Kumar","QA Engineer","QA",13,"ACTIVE",40),
                        new Employee(26,"Simran Kaur","Mobile Developer","Mobile",13,"ACTIVE",40),
                        new Employee(27,"Aditya Rao","Security Engineer","Security",13,"ACTIVE",40),
                        new Employee(28,"Pooja Nair","Full Stack Developer","Engineering",13,"ACTIVE",40),

                        // ================= TEAM 2 =================
                        new Employee(14,"Neha Kapoor","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(29,"Aman Gupta","Frontend Developer","Engineering",14,"ACTIVE",40),
                        new Employee(30,"Riya Sen","Backend Developer","Engineering",14,"ACTIVE",40),
                        new Employee(31,"Siddharth Jain","AI Engineer","AI",14,"ACTIVE",40),
                        new Employee(32,"Megha Kapoor","DevOps Engineer","Cloud",14,"ACTIVE",40),
                        new Employee(33,"Nikhil Arora","QA Engineer","QA",14,"ACTIVE",40),
                        new Employee(34,"Ishita Roy","Mobile Developer","Mobile",14,"ACTIVE",40),
                        new Employee(35,"Dev Malhotra","Security Engineer","Security",14,"ACTIVE",40),
                        new Employee(36,"Tanya Bansal","Full Stack Developer","Engineering",14,"ACTIVE",40),

                        // ================= TEAM 3 =================
                        new Employee(15,"Rahul Verma","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(37,"Ankit Mishra","AI Engineer","AI",15,"ACTIVE",40),
                        new Employee(38,"Shreya Ghosh","Data Scientist","AI",15,"ACTIVE",40),
                        new Employee(39,"Ravi Iyer","Backend Developer","Engineering",15,"ACTIVE",40),
                        new Employee(40,"Divya Nair","Frontend Developer","Engineering",15,"ACTIVE",40),
                        new Employee(41,"Kunal Singh","DevOps Engineer","Cloud",15,"ACTIVE",40),
                        new Employee(42,"Snehal Patil","QA Engineer","QA",15,"ACTIVE",40),
                        new Employee(43,"Yash Agarwal","Mobile Developer","Mobile",15,"ACTIVE",40),
                        new Employee(44,"Komal Verma","Full Stack Developer","Engineering",15,"ACTIVE",40),

                        // ================= TEAM 4 =================
                        new Employee(16,"Sneha Iyer","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(45,"Rajat Gupta","DevOps Engineer","Cloud",16,"ACTIVE",40),
                        new Employee(46,"Deepak Yadav","Backend Developer","Engineering",16,"ACTIVE",40),
                        new Employee(47,"Manish Kumar","Frontend Developer","Engineering",16,"ACTIVE",40),
                        new Employee(48,"Aditi Sharma","AI Engineer","AI",16,"ACTIVE",40),
                        new Employee(49,"Saurabh Jain","QA Engineer","QA",16,"ACTIVE",40),
                        new Employee(50,"Harsh Patel","Mobile Developer","Mobile",16,"ACTIVE",40),
                        new Employee(51,"Nitin Joshi","Security Engineer","Security",16,"ACTIVE",40),
                        new Employee(52,"Richa Singh","Full Stack Developer","Engineering",16,"ACTIVE",40),

                        // ================= TEAM 5 =================
                        new Employee(17,"Arjun Mehta","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(53,"Varun Kapoor","Mobile Developer","Mobile",17,"ACTIVE",40),
                        new Employee(54,"Sakshi Jain","Frontend Developer","Engineering",17,"ACTIVE",40),
                        new Employee(55,"Rohit Das","Backend Developer","Engineering",17,"ACTIVE",40),
                        new Employee(56,"Neha Gupta","AI Engineer","AI",17,"ACTIVE",40),
                        new Employee(57,"Aditya Singh","DevOps Engineer","Cloud",17,"ACTIVE",40),
                        new Employee(58,"Pallavi Roy","QA Engineer","QA",17,"ACTIVE",40),
                        new Employee(59,"Kritika Sharma","Security Engineer","Security",17,"ACTIVE",40),
                        new Employee(60,"Gaurav Mehta","Full Stack Developer","Engineering",17,"ACTIVE",40),

                        // ================= TEAM 6 =================
                        new Employee(18,"Priya Nair","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(61,"Abhishek Verma","Security Engineer","Security",18,"ACTIVE",40),
                        new Employee(62,"Kiran Nair","Backend Developer","Engineering",18,"ACTIVE",40),
                        new Employee(63,"Rahul Das","Frontend Developer","Engineering",18,"ACTIVE",40),
                        new Employee(64,"Sneha Roy","AI Engineer","AI",18,"ACTIVE",40),
                        new Employee(65,"Anurag Singh","DevOps Engineer","Cloud",18,"ACTIVE",40),
                        new Employee(66,"Priti Shah","QA Engineer","QA",18,"ACTIVE",40),
                        new Employee(67,"Vikas Yadav","Mobile Developer","Mobile",18,"ACTIVE",40),
                        new Employee(68,"Meena Iyer","Full Stack Developer","Engineering",18,"ACTIVE",40),

                        // ================= TEAM 7 =================
                        new Employee(19,"Vikram Singh","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(69,"Ramesh Gupta","QA Engineer","QA",19,"ACTIVE",40),
                        new Employee(70,"Sunita Sharma","Backend Developer","Engineering",19,"ACTIVE",40),
                        new Employee(71,"Ajay Kumar","Frontend Developer","Engineering",19,"ACTIVE",40),
                        new Employee(72,"Geeta Verma","AI Engineer","AI",19,"ACTIVE",40),
                        new Employee(73,"Anil Singh","DevOps Engineer","Cloud",19,"ACTIVE",40),
                        new Employee(74,"Kavita Nair","Mobile Developer","Mobile",19,"ACTIVE",40),
                        new Employee(75,"Ritu Jain","Security Engineer","Security",19,"ACTIVE",40),
                        new Employee(76,"Mohit Arora","Full Stack Developer","Engineering",19,"ACTIVE",40),

                        // ================= TEAM 8 =================
                        new Employee(20,"Ananya Das","Engineering Manager","Engineering",11,"ACTIVE",40),

                        new Employee(77,"Rahul Mehta","Full Stack Developer","Engineering",20,"ACTIVE",40),
                        new Employee(78,"Ananya Sharma","Frontend Developer","Engineering",20,"ACTIVE",40),
                        new Employee(79,"Kunal Verma","Backend Developer","Engineering",20,"ACTIVE",40),
                        new Employee(80,"Isha Kapoor","AI Engineer","AI",20,"ACTIVE",40),
                        new Employee(81,"Rohit Jain","DevOps Engineer","Cloud",20,"ACTIVE",40),
                        new Employee(82,"Pooja Singh","QA Engineer","QA",20,"ACTIVE",40),
                        new Employee(83,"Vivek Gupta","Mobile Developer","Mobile",20,"ACTIVE",40),
                        new Employee(84,"Sneha Das","Security Engineer","Security",20,"ACTIVE",43)
                );

                for(Employee emp: employees)
                    System.out.println(employeeService.createEmployee(emp));
                break;
            default:
                System.out.println("Invalid Input!");
        }
    }
}
