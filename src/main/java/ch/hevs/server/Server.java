package ch.hevs.server;

/**
 * Classe qui contient un main pour créer un exécutable serveur.
 */
public class Server {

    public static void main(String[] args) {
        ServerConnexion server = new ServerConnexion();
        Thread t = new Thread(server);
        t.start();
    }
}
