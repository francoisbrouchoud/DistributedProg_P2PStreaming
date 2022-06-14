package ch.hevs.server;

import ch.hevs.client.PairToPair;
import ch.hevs.common.ServerBase;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

/***
 * Role:
 * - Etre à l'écoute des clients
 * - Stock une liste de fichiers avec ip et port de lieu
 * - Dire l'ip et port pour retrouver un fichier
 * - Lister les fichiers dispo
 */
public class Server extends ServerBase {

    public Server(ServerSocket socketServer) {
        super(socketServer);
    }

    public static void main(String[] args) {
        try {
            Server s = new Server(new ServerSocket());
            ServerSocket mySkServer = s.createServer();
            ServerConnexion server = new ServerConnexion(mySkServer);
            Thread t = new Thread(server);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void acceptClient(Socket socket) {

    }
}
