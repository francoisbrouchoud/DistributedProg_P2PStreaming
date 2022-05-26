package ch.hevs.server;

import ch.hevs.common.ClientInfo;
import ch.hevs.common.ServerBase;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerConnexion extends ServerBase {
    ArrayBlockingQueue<ClientInfo> clients;
    public ServerConnexion(ServerSocket socketServer) {
        super(socketServer);
        clients = new ArrayBlockingQueue<>(20);
    }

    @Override
    public void acceptClient(Socket socket) {
        Thread t = new Thread(new ServerClientConnexion(socket, clients));
        t.start();
    }
}
