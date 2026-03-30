package com.optiflow.networking;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while(true)
            {
                Message message = (Message) in.readObject();
                SocketServer.broadcast(message);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message)
    {
        try
        {
            out.writeObject(message);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
