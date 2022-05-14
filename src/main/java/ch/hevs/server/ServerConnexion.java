package ch.hevs.server;

import ch.hevs.common.ClientInfo;
import ch.hevs.common.ServerBase;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerConnexion extends ServerBase {
    public ServerConnexion(ServerSocket socketServer) {
        super(socketServer);
    }

    @Override
    public void acceptClient(Socket socket) {
        ArrayBlockingQueue<ClientInfo> clients = new ArrayBlockingQueue<>(20);
        Thread t = new Thread(new ServerClientConnexion(socket, clients));
        t.start();
    }
}
