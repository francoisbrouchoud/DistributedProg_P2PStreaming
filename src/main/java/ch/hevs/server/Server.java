package ch.hevs.server;

import ch.hevs.client.PairToPair;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;

/***
 * Role:
 * - Etre à l'écoute des clients
 * - Stock une liste de fichiers avec ip et port de lieu
 * - Dire l'ip et port pour retrouver un fichier
 * - Lister les fichiers dispo
 */
public class Server {

    Socket srvSocket = null;
    InetAddress localAddress = null;
    PrintWriter pout;
    Scanner scan;

    public static void main(String[] args) {

        //TODO scan to find current ip and port

        ServerSocket mySkServer = new ServerSocket(45007,10,localAddress);

        ServerConnexion server = new ServerConnexion(mySkServer);
        Thread t = new Thread(server);

        t.start();
    }
}
