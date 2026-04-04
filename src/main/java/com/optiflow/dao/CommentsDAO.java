package com.optiflow.dao;

import com.optiflow.database.DBConnection;
import com.optiflow.models.Comments;
import com.optiflow.models.Skills;
import com.optiflow.services.AuditLogService;
import com.optiflow.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CommentsDAO
{
    private AuditLogService auditLogService;

    public CommentsDAO()
    {
        this.auditLogService = new AuditLogService();
    }
    public boolean addComment(int task_id, int user_id, String content) throws SQLException
    {
        String sql = "INSERT INTO comments (task_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, task_id);
            stmt.setInt(2, user_id);
            stmt.setString(3, content);
            int rows = stmt.executeUpdate();

            if(rows > 0)
            {
                ResultSet rs = stmt.getGeneratedKeys();

                if (rs.next())
                {
                    int generatedId = rs.getInt(1);
                    auditLogService.logAction(SessionManager.getUser().getUserId(), "ADD_COMMENT", "COMMENT", generatedId, SessionManager.getUser().getName()+" added a comment");
                }
            }

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Comments> getCommentsByTask(int task_id) throws SQLException
    {
        LinkedList<Comments> commentList = new LinkedList<>();
        String sql = "SELECT * FROM comments WHERE task_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, task_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Comments comment = new Comments();
                comment.setTask_id(rs.getInt("task_id"));
                comment.setUser_id(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));

                commentList.add(comment);
                return commentList;
            }
        }

        return null;
    }

    public List<Comments> getCommentsByUser(int user_id) throws SQLException
    {
        LinkedList<Skills> skillList = new LinkedList<>();
        LinkedList<Comments> commentList = new LinkedList<>();
        String sql = "SELECT * FROM comments WHERE user_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                Comments comment = new Comments();
                comment.setTask_id(rs.getInt("task_id"));
                comment.setUser_id(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));

                commentList.add(comment);
                return commentList;
            }
        }

        return null;
    }

    public boolean updateComment(int comment_id, String content) throws SQLException
    {
        if(comment_id <=0 || content.isEmpty())
            return false;

        String sql = "UPDATE comments SET content=? WHERE comment_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setString(1, content);
            stmt.setInt(2, comment_id);

            rs = stmt.executeUpdate();
        }

        return rs == 1;
    }

    public int deleteComment(int comment_id) throws SQLException
    {
        String sql = "DELETE FROM comments WHERE comment_id=?";
        int rs;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, comment_id);

            rs = stmt.executeUpdate();
        }

        return rs;
    }
}
