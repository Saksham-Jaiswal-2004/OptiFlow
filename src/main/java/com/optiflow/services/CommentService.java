package com.optiflow.services;

import com.optiflow.dao.CommentsDAO;
import com.optiflow.models.Comments;
import com.optiflow.networking.Message;
import com.optiflow.networking.MessageType;
import com.optiflow.utils.AppContext;
import com.optiflow.utils.SessionManager;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CommentService
{
    private CommentsDAO commentsDAO;
    private AuditLogService auditLogService;

    CommentService()
    {
        this.commentsDAO = new CommentsDAO();
        this.auditLogService = new AuditLogService();
    }

    public boolean addComment(Comments comment) throws Exception
    {
        if(comment == null)
            return false;

        if(commentsDAO.addComment(comment.getTask_id(), comment.getUser_id(), comment.getContent()))
        {
            AppContext.socketClient.sendMessage(
                    new Message(MessageType.COMMENT, comment.getContent(), SessionManager.getUser().getUserId(), comment.getTask_id(), "TASK")
            );

            auditLogService.logAction(SessionManager.getUser().getUserId(), "ADD_COMMENT", "COMMENT", comment.getComment_id(), SessionManager.getUser().getName()+" added a comment");

            return true;
        }
        else
        {
            return false;
        }
    }

    public List<Comments> getCommentsByTask(int task_id) throws SQLException
    {
        if(task_id <= 0)
            return null;

        return commentsDAO.getCommentsByTask(task_id);
    }

    public boolean updateComment(Comments comment) throws SQLException
    {
        auditLogService.logAction(SessionManager.getUser().getUserId(), "UPDATE_COMMENT", "COMMENT", comment.getComment_id(), SessionManager.getUser().getName()+"  updated a comment");
        return commentsDAO.updateComment(comment.getComment_id(), comment.getContent());
    }

    public boolean deleteComment(int comment_id) throws SQLException
    {
        if(comment_id <= 0)
            return false;

        auditLogService.logAction(SessionManager.getUser().getUserId(), "DELETE_COMMENT", "COMMENT", comment_id, SessionManager.getUser().getName()+" deleted a comment");
        return commentsDAO.deleteComment(comment_id) == 1;
    }

}
