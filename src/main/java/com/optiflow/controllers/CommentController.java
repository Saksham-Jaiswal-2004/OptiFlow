package com.optiflow.controllers;

import com.optiflow.models.Comments;
import com.optiflow.services.CommentService;

import java.util.Collections;
import java.util.List;

public class CommentController
{
    private final CommentService commentService;

    public CommentController() {
        this.commentService = new CommentService();
    }

    public boolean addComment(int taskId, String content)
    {
        try {
            if (content == null || content.isBlank() || taskId <= 0) {
                return false;
            }

            Comments comment = new Comments(taskId, 0, content.trim());
            return commentService.addComment(comment);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateComment(int commentId, String content)
    {
        try {
            if (commentId <= 0 || content == null || content.isBlank()) {
                return false;
            }

            Comments comment = new Comments();
            comment.setContent(content.trim());
            comment.setTask_id(1);
            comment.setUser_id(1);

            // service expects comment id on model, preserving existing API without changing services
            java.lang.reflect.Field idField = Comments.class.getDeclaredField("comment_id");
            idField.setAccessible(true);
            idField.setInt(comment, commentId);

            return commentService.updateComment(comment);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteComment(int commentId)
    {
        try {
            return commentService.deleteComment(commentId);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Comments> getCommentsByTask(int taskId)
    {
        try {
            List<Comments> comments = commentService.getCommentsByTask(taskId);
            return comments == null ? Collections.emptyList() : comments;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
