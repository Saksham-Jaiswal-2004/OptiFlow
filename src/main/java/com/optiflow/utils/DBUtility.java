package com.optiflow.utils;

import com.optiflow.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtility
{
    public int getCount(String query, int managerId) throws SQLException
    {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setInt(1, managerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return rs.getInt(1);
        }

        return 0;
    }
}
