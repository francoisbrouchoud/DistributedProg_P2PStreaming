package ch.hevs.client;

import java.net.InetAddress;
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
    public static String serverName;

    public static void main(String[] args) {
        serverName = args[0];
    }
}
