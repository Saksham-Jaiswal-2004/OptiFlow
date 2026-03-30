package com.optiflow.utils;

import com.optiflow.networking.SocketClient;

public class AppContext
{
    public static SocketClient socketClient;

    public static void initSocket()
    {
        try
        {
            socketClient = new SocketClient();
            socketClient.connect();
            System.out.println("Socket connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SocketClient getSocketClient()
    {
        return socketClient;
    }
}
