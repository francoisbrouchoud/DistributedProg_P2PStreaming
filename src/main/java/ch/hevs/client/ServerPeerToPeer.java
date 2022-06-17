package ch.hevs.client;

import ch.hevs.common.ServerBase;

import java.net.Socket;
import java.util.logging.Logger;

public class ServerPeerToPeer extends ServerBase {

    Logger LOGGER;

    //Constructor
    public ServerPeerToPeer(Logger LOGGER) {
        super();
        this.LOGGER = LOGGER;
    }

    @Override
    public void acceptClient(Socket socket) {
        Thread t = new Thread(new PeerToPeer(socket, nbrClient, this.LOGGER));
        LOGGER.info("New Client connected : " + nbrClient);
        t.start();
    }
}
