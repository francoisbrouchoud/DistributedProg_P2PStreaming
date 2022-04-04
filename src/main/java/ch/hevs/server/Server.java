package ch.hevs.server;

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
        int port = Integer.parseInt(args[0]);
        ServerSocket mySkServer = null;
        try {
            mySkServer = new ServerSocket(port);
            System.out.println("Used IpAddress :" + mySkServer.getInetAddress());
            System.out.println("Listening to Port :" + mySkServer.getLocalPort());

            while (true){
                mySkServer.accept();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
