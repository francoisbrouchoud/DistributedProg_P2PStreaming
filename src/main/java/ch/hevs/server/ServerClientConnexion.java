package ch.hevs.server;

import java.net.Socket;

public class ServerClientConnexion implements Runnable{
    private Socket clientSocketOnServer;
    private int clientNumber;

    //Constructor
    public ServerClientConnexion (Socket clientSocketOnServer, int clientNo)
    {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientNumber = clientNo;
    }
    @Override
    public void run() {
        //TODO écoute la commande

        //TODO switch case des commande possible
        //Création de fonction par cas
    }
}
