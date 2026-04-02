package com.optiflow.database;

import java.sql.Connection;
import java.sql.DriverManager;
import com.optiflow.config.ConfigLoader;

public class DBConnection
{

    public static Connection getConnection()
    {

        try
        {
            String url = ConfigLoader.getProperty("db.url");
            String user = ConfigLoader.getProperty("db.username");
            String password = ConfigLoader.getProperty("db.password");
            System.out.println("DATABASE CONNECTION SUCCESSFULL!");

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("DATABASE CONNECTION FAILED!");
            e.printStackTrace();
        }

        return null;
    }
}
