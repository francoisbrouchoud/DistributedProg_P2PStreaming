package ch.hevs.server;

/***
 * Role:
 * - Etre à l'écoute des clients
 * - Stock une liste de fichiers avec ip et port de lieu
 * - Dire l'ip et port pour retrouver un fichier
 * - Lister les fichiers dispo
 */
public class Server {

    public static void main(String[] args) {
        ServerConnexion server = new ServerConnexion();
        Thread t = new Thread(server);
        t.start();
    }
}
