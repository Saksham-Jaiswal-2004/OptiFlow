package com.optiflow.networking;

import com.optiflow.dao.UserDAO;

import java.net.*;
import java.io.*;

public class SocketClient
{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MessageListener listener;
    private UserDAO userDAO;

    public void setListener(MessageListener listener)
    {
        this.listener = listener;
        this.userDAO = new UserDAO();
    }

    public void connect() throws Exception
    {
        socket = new Socket("192.168.56.1", 5000);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        new Thread(() -> listen()).start();
    }

    public void sendMessage(Message message) throws Exception
    {
        out.writeObject(message);
    }

    private void listen()
    {
        try
        {
            while(true)
            {
                Message message = (Message) in.readObject();
                System.out.println("New: " + message.getContent());

                if(listener != null)
                {
                    listener.onMessageReceived(message);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Listener is null!");
        }
    }
}
