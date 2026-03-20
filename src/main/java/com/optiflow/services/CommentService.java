package com.optiflow.services;

import com.optiflow.models.Comments;

import java.util.LinkedList;
import java.util.List;

public class CommentService
{
    public boolean addComment(Comments comment)
    {
        return true;
    }

    public List<Comments> getCommentsByTask(int taskId)
    {
        LinkedList<Comments> commentList = new LinkedList<>();

        return commentList;
    }

    public boolean updateComment(Comments comment)
    {
        return true;
    }

    public boolean deleteComment(int commentId)
    {
        return true;
    }

}
