package ch.hevs.client;

import ch.hevs.common.ServerBase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPairToPair extends ServerBase {

    //Constructor
    public ServerPairToPair(){
        super();
    }

    @Override
    public void acceptClient(Socket socket){
        Thread t = new Thread(new PairToPair(socket, nbrClient));
        t.start();
    }
}
