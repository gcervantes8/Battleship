package edu.utep.cs4330.battleship;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jerry C on 4/9/2017.
 */

public class SocketHandler {
    private static Socket socket;

    public static synchronized void setSocket(Socket s){
        socket = s;
    }

    public static synchronized Socket getSocket(){
        return socket;
    }

}
