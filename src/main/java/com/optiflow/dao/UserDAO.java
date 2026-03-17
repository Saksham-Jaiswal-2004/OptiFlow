package com.optiflow.dao;

import java.sql.*;
import com.optiflow.database.DBConnection;
import com.optiflow.models.User;

public class UserDAO
{
    public void addUser(String name, String email, String passwordHash, String role) throws SQLException
    {
        String sql = "INSERT INTO Users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, role);
            stmt.executeUpdate();
        }
    }

    public User getUserByEmail(String email) throws SQLException
    {

        String sql = "SELECT * FROM Users WHERE email=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1,email);

            ResultSet rs = stmt.executeQuery();

            if(rs.next())
            {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }

        return null;
    }
}
