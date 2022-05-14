package ch.hevs.server;

import ch.hevs.client.PairToPair;
import ch.hevs.common.ServerBase;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnexion extends ServerBase {
    public ServerConnexion(ServerSocket socketServer) {
        super(socketServer);
    }

    @Override
    public void acceptClient(Socket socket) {
        Thread t = new Thread(new ServerClientConnexion(socket,nbrClient));
        t.start();
    }
}
