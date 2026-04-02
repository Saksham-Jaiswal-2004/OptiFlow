package com.optiflow.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optiflow.models.Comments;
import com.optiflow.models.User;
import com.optiflow.services.CommentService;
import com.optiflow.services.UserService;
import com.optiflow.utils.AppContext;
import com.optiflow.utils.SessionManager;

import java.sql.SQLException;
import java.util.Scanner;

public class CommentTest
{
    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        CommentService commentService = new CommentService();
        UserService userService = new UserService();
        SessionManager.setUser(userService.getUserById(6));

        AppContext.initSocket();

        System.out.println("********    Comment Test    ********");
        System.out.println("1. Add a Comment");
        System.out.println("2. Get Comments by Task");
//        System.out.println("3. Update a Comment");
        System.out.println("3. Delete a Comment");
        System.out.print("Enter your choice: ");
        int ch = sc.nextInt();

        switch (ch)
        {
            case 1: System.out.print("Enter Task-Id: ");
                int taskId = sc.nextInt();
                System.out.print("Enter User-Id: ");
                int userId = sc.nextInt();
                System.out.print("Enter Content: ");
                String content = sc.next();

                Comments comment = new Comments(taskId, userId, content);
                commentService.addComment(comment);
            break;
            case 2: System.out.print("Enter Task-Id: ");
                int taskID = sc.nextInt();

                for(Comments c: commentService.getCommentsByTask(taskID))
                {
                    System.out.println(c.getComment_id()+" "+c.getTask_id()+" "+c.getUser_id()+" "+c.getContent());
                }
            break;
            case 3: System.out.print("Enter Comment-Id: ");
                int commentId = sc.nextInt();

                if(commentService.deleteComment(commentId))
                    System.out.println("Comment Deleted Successfully!");
            break;
            default: System.out.println("Invalid Input!");
        }
    }
}
