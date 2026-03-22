package com.optiflow.dao;

import java.sql.*;
import java.util.*;
import com.optiflow.database.DBConnection;
import com.optiflow.models.User;

public class UserDAO
{
    public boolean addUser(String name, String email, String passwordHash, String role) throws SQLException
    {
        String sql = "INSERT INTO Users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, role);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public User getUserById(int user_id) throws SQLException
    {
        String sql = "SELECT * FROM Users WHERE user_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, user_id);

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

    public List<User> getAllUsers() throws SQLException
    {
        LinkedList<User> userList = new LinkedList<>();
        String sql = "SELECT * FROM users";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));

                userList.add(user);
            }
        }
        return userList;
    }

    public List<User> getUsersByRole(String role) throws SQLException
    {
        LinkedList<User> userList = new LinkedList<>();
        String sql = "SELECT * FROM users WHERE role=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));

                userList.add(user);
            }
        }
        return userList;
    }

    public int updateName(int user_id, String name) throws SQLException
    {
        String sql = "UPDATE users SET name=? WHERE user_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, name);
            stmt.setInt(2, user_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateEmail(int user_id, String email) throws SQLException
    {
        String sql = "UPDATE users SET email=? WHERE user_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, email);
            stmt.setInt(2, user_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updatePassword(int user_id, String password_hash) throws SQLException
    {
        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, password_hash);
            stmt.setInt(2, user_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public int updateRole(int user_id, String role) throws SQLException
    {
        String sql = "UPDATE users SET role=? WHERE user_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, role);
            stmt.setInt(2, user_id);

            rs = stmt.executeUpdate();
            System.out.println("Result: "+rs);
        }

        return rs;
    }

    public boolean deleteUser(int user_id) throws SQLException
    {
        String sql = "DELETE FROM Users WHERE user_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, user_id);

            rs = stmt.executeUpdate();
        }

        if(rs!=0)
            return true;

        return false;
    }
}
