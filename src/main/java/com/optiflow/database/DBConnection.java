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

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
