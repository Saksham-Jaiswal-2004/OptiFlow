package com.optiflow.networking;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketServer
{

    static List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception
    {

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server Started...");

        while(true)
        {
            Socket socket = serverSocket.accept();

            ClientHandler client = new ClientHandler(socket);
            clients.add(client);

            new Thread(client).start();
        }
    }

    public static void broadcast(Message message)
    {
        for (ClientHandler client : clients)
        {
            try
            {
                client.sendMessage(message);
            } catch (Exception e) {
                clients.remove(client);
            }
        }
    }
}
