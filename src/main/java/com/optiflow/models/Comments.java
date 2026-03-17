package com.optiflow.models;

public class Comments
{
    private int comment_id;
    private int task_id;
    private int user_id;
    private String content;

    Comments()
    {
        System.out.println("Mai Comments Hu!");
    }

    Comments(int task_id, int user_id, String content)
    {
        this.task_id = task_id;
        this.user_id = user_id;
        this.content = content;
    }

    public int getComment_id()
    {
        return comment_id;
    }

    public int getTask_id()
    {
        return task_id;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public String getContent()
    {
        return content;
    }

    public void setTask_id(int task_id)
    {
        this.task_id = task_id;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
