package com.optiflow.networking;

import java.net.*;
import java.io.*;

public class SocketClient
{

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect() throws Exception
    {
        socket = new Socket("localhost", 5000);

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
                System.out.println("New Message: " + message.getContent());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
