package com.optiflow.tests;

import com.optiflow.models.User;
import com.optiflow.services.AuthService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AuthTest
{
    public static void main(String[] args) throws SQLException
    {
        Scanner sc = new Scanner(System.in);
        AuthService authService = new AuthService();

        System.out.println("********    Auth Test    ********");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter your choice: ");
        int ch = sc.nextInt();
        sc.nextLine();

        switch (ch)
        {
            case 1: System.out.println("Login");
            break;
            case 2:
                List<User> users = List.of(
                        new User("Saksham Jaiswal", "saksham@technova.com", "Saksham*1234", "ADMIN"),
                        new User("Pratuman Kumar", "pratuman@technova.com", "Pratuman*1234", "ADMIN"),

                        new User("Amit Sharma", "amit.sharma@technova.com", "Manager*1234", "MANAGER"),
                        new User("Neha Kapoor", "neha.kapoor@technova.com", "Manager*1234", "MANAGER"),
                        new User("Rahul Verma", "rahul.verma@technova.com", "Manager*1234", "MANAGER"),
                        new User("Sneha Iyer", "sneha.iyer@technova.com", "Manager*1234", "MANAGER"),
                        new User("Arjun Mehta", "arjun.mehta@technova.com", "Manager*1234", "MANAGER"),
                        new User("Priya Nair", "priya.nair@technova.com", "Manager*1234", "MANAGER"),
                        new User("Vikram Singh", "vikram.singh@technova.com", "Manager*1234", "MANAGER"),
                        new User("Ananya Das", "ananya.das@technova.com", "Manager*1234", "MANAGER"),

                        new User("Rohit Verma","rohit.verma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Karan Mehta","karan.mehta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Priya Shah","priya.shah@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Arjun Das","arjun.das@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Neeraj Kumar","neeraj.kumar@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Simran Kaur","simran.kaur@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Aditya Rao","aditya.rao@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Pooja Nair","pooja.nair@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Aman Gupta","aman.gupta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Riya Sen","riya.sen@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Siddharth Jain","siddharth.jain@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Megha Kapoor","megha.kapoor@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Nikhil Arora","nikhil.arora@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ishita Roy","ishita.roy@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Dev Malhotra","dev.malhotra@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Tanya Bansal","tanya.bansal@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ankit Mishra","ankit.mishra@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Shreya Ghosh","shreya.ghosh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ravi Iyer","ravi.iyer@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Divya Nair","divya.nair@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Kunal Singh","kunal.singh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Snehal Patil","snehal.patil@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Yash Agarwal","yash.agarwal@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Komal Verma","komal.verma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Rajat Gupta","rajat.gupta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Deepak Yadav","deepak.yadav@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Manish Kumar","manish.kumar@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Aditi Sharma","aditi.sharma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Saurabh Jain","saurabh.jain@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Harsh Patel","harsh.patel@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Nitin Joshi","nitin.joshi@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Richa Singh","richa.singh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Varun Kapoor","varun.kapoor@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Sakshi Jain","sakshi.jain@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Rohit Das","rohit.das@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Neha Gupta","neha.gupta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Aditya Singh","aditya.singh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Pallavi Roy","pallavi.roy@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Kritika Sharma","kritika.sharma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Gaurav Mehta","gaurav.mehta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Abhishek Verma","abhishek.verma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Kiran Nair","kiran.nair@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Rahul Das","rahul.das@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Sneha Roy","sneha.roy@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Anurag Singh","anurag.singh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Priti Shah","priti.shah@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Vikas Yadav","vikas.yadav@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Meena Iyer","meena.iyer@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ramesh Gupta","ramesh.gupta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Sunita Sharma","sunita.sharma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ajay Kumar","ajay.kumar@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Geeta Verma","geeta.verma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Anil Singh","anil.singh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Kavita Nair","kavita.nair@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ritu Jain","ritu.jain@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Mohit Arora","mohit.arora@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Rahul Mehta","rahul.mehta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Ananya Sharma","ananya.sharma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Kunal Verma","kunal.verma@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Isha Kapoor","isha.kapoor@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Rohit Jain","rohit.jain@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Pooja Singh","pooja.singh@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Vivek Gupta","vivek.gupta@technova.com","Employee*1234","EMPLOYEE"),
                        new User("Sneha Das","sneha.das@technova.com","Employee*1234","EMPLOYEE")
                );

                for(User u: users)
                    authService.register(u);
            break;
            default: System.out.println("Invalid Input!");
        }
    }
}
