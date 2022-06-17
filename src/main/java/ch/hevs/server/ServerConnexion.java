package ch.hevs.server;

import ch.hevs.common.ClientInfo;
import ch.hevs.common.LogHelper;
import ch.hevs.common.ServerBase;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class ServerConnexion extends ServerBase {
    ArrayBlockingQueue<ClientInfo> clients;
    private static Logger LOGGER;

    public ServerConnexion() {
        super();
        clients = new ArrayBlockingQueue<>(20);
        // Config logger
        LOGGER = LogHelper.loggerConfig(ServerConnexion.class.getName());
    }

    /**
     * Acceptation du client et création d'un thread
     * @param socket
     */
    @Override
    public void acceptClient(Socket socket) {
        Thread t = new Thread(new ServerClientConnexion(socket, clients, LOGGER));
        LOGGER.info("New Client connected");
        t.start();
    }
}
