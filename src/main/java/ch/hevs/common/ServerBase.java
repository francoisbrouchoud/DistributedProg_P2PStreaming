package ch.hevs.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ServerBase implements Runnable{
    protected ServerSocket socketServer;
    protected Socket socket;
    protected int nbrClient;

    public ServerBase(ServerSocket socketServer){
        //TODO create socket server here?
        this.socketServer = socketServer;
    }
    public abstract void acceptClient(Socket socket);
    @Override
    public void run() {
        try {
            //infinite loop
            while(true){
                socket = socketServer.accept(); // A client wants to connect, we accept him
                acceptClient(socket);

                System.out.println("Client Nr "+nbrClient+ " is connected");
                nbrClient++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
