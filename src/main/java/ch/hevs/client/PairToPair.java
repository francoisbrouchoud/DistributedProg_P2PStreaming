package ch.hevs.client;

import ch.hevs.common.ActionClientServer;
import ch.hevs.common.ActionP2P;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

        try {
            // création des reader et des writer
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocketOnServer.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocketOnServer.getOutputStream());
            // écoute la commande
            int orderNumber = Integer.parseInt(buffIn.readLine());
            ActionP2P order = ActionP2P.values()[orderNumber];


            //TODO switch case des commandes possible
            // Création de fonction par cas
            switch (order){
                case GET_AUDIO_FILE:
                    break;
            }
            clientSocketOnServer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
