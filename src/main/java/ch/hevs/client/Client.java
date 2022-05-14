package ch.hevs.client;

import ch.hevs.server.ServerConnexion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * Role:
 * - Demander au server une liste de fichier dispo
 * - Envoyer la liste de fichier dispo sur notre ordi
 * - Récupérer un fichier
 *      - Ecouter le fichier Si music ???
 *      - Download le fichier Si document ???
 */
public class Client {
    Socket clientSocket;
    InetAddress serverAddress;
    public static String serverIp;
    public static int serverPort;

    public static void main(String[] args) {
        //TODO scan to find current ip and port

        // saisit par l'utilisateur
        serverIp = args[0];
        serverPort = Integer.parseInt(args[1]);
        // activer l'écoute de connexion
        ServerSocket mySkServer = null;
        try {
            mySkServer = new ServerSocket(450000,10,ip);
            ServerPairToPair server = new ServerPairToPair(mySkServer);
            Thread t = new Thread(server);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Afficher un menu dans la console
        // switch case en fonction des choix
        //

    }
}
