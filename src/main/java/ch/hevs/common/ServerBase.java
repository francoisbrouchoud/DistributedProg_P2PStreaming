package ch.hevs.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class ServerBase implements Runnable{
    protected ServerSocket socketServer;
    protected int nbrClient;
    public static InetAddress serverAddress;
    public static int serverPort ;

    public ServerBase(ServerSocket socketServer){
        //TODO create socket server here?
        this.socketServer = socketServer;
    }

    public static InetAddress getServerAddress() {
        try {
            return InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return serverAddress;
    }

    public static int getServerPort() {
        return 56000;
        //return serverPort;
    }

    public abstract void acceptClient(Socket socket);
    @Override
    public void run() {
        try {
            //infinite loop
            while(true){
                Socket socket = socketServer.accept(); // A client wants to connect, we accept him
                acceptClient(socket);

                System.out.println("Client Nr "+nbrClient+ " is connected");
                nbrClient++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
