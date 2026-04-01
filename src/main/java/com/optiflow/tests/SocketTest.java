package com.optiflow.tests;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.User;
import com.optiflow.networking.Message;
import com.optiflow.networking.MessageType;
import com.optiflow.utils.AppContext;
import com.optiflow.utils.SessionManager;

import java.sql.SQLException;
import java.util.Scanner;

public class SocketTest
{
    private UserDAO userDAO;

    public SocketTest()
    {
        this.userDAO = new UserDAO();
    }

    public void chat(User user) throws Exception
    {
        while(true)
        {
            Scanner sc = new Scanner(System.in);
            System.out.print(user.getName()+": ");
            String chat = sc.nextLine();

            AppContext.initSocket();

            AppContext.socketClient.sendMessage(
                    new Message(MessageType.COMMENT, chat, user.getUserId(), 0, "TASK")
            );
        }
    }

    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO1 = new UserDAO();
        SocketTest socketTest = new SocketTest();

        System.out.print("Enter User Id: ");
        int u = sc.nextInt();

        socketTest.chat(userDAO1.getUserById(u));
    }
}
