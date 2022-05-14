package ch.hevs.client;

import java.net.Socket;

public class PairToPair implements Runnable{
    private Socket clientSocketOnServer;
    private int clientNumber;

    //Constructor
    public PairToPair (Socket clientSocketOnServer, int clientNo)
    {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientNumber = clientNo;

    }
    @Override
    public void run() {
        // TODO écoute la commande

        //TODO switch case des commandes possible
        // Création de fonction par cas
    }
}
