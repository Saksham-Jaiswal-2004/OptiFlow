package com.optiflow.services;

import com.optiflow.dao.CommentsDAO;
import com.optiflow.models.Comments;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CommentService
{
    private CommentsDAO commentsDAO;

    CommentService()
    {
        this.commentsDAO = new CommentsDAO();
    }

    public boolean addComment(Comments comment) throws SQLException
    {
        if(comment == null)
            return false;

        return commentsDAO.addComment(comment.getTask_id(), comment.getUser_id(), comment.getContent());
    }

    public List<Comments> getCommentsByTask(int task_id) throws SQLException
    {
        if(task_id <= 0)
            return null;

        return commentsDAO.getCommentsByTask(task_id);
    }

//    public boolean updateComment(Comments comment)
//    {
//        return true;
//    }

    public boolean deleteComment(int comment_id) throws SQLException
    {
        if(comment_id <= 0)
            return false;

        return commentsDAO.deleteComment(comment_id) == 1;
    }

}
