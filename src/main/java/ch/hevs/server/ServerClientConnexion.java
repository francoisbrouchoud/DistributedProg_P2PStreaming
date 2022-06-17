package ch.hevs.server;

import ch.hevs.common.ActionClientServer;
import ch.hevs.common.ClientInfo;
import ch.hevs.common.FileInfo;
import ch.hevs.common.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ServerClientConnexion implements Runnable {
    private Socket clientSocketOnServer;
    private String clientId;
    private ArrayBlockingQueue<ClientInfo> clients;
    private Logger LOGGER;

    public ServerClientConnexion(Socket clientSocketOnServer, ArrayBlockingQueue<ClientInfo> clients, Logger logger) {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientId = clientSocketOnServer.getInetAddress() + ":" + clientSocketOnServer.getPort();
        this.clients = clients;
        this.LOGGER = logger;
    }

    @Override
    public void run() {
        try {
            // Création des reader et des writer
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocketOnServer.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocketOnServer.getOutputStream(), true);

            // Ecoute la commande
            int orderNumber = Integer.parseInt(buffIn.readLine());
            ActionClientServer order = ActionClientServer.values()[orderNumber];


            // Création de fonction par cas
            switch (order) {
                case TEST_CONNEXION:
                    this.LOGGER.info("Test connexion");
                    break;
                case GET_FILES_LIST:
                    this.LOGGER.info("Get files list");
                    getFilesList(pOut);
                    break;
                case SHARE_FILE_LIST:
                    this.LOGGER.info("Share files list");
                    shareFilesList(buffIn);
                    break;
                case LOGOUT:
                    this.LOGGER.info("Logout");
                    deleteClient(buffIn);
                    break;
            }
            clientSocketOnServer.close();

        } catch (IOException e) {
            LogHelper.LogError(e, this.LOGGER);
        }
    }

    /**
     * Suppression de clients sur le serveur
     * @param buffIn
     */
    private void deleteClient(BufferedReader buffIn) {
        try {
            String ip = buffIn.readLine();
            int port = Integer.parseInt(buffIn.readLine());

            clients.removeIf(new Predicate<ClientInfo>() {
                @Override
                public boolean test(ClientInfo client) {
                    return client.getClientAddress().equals(ip) && client.getClientPort() == port;
                }
            });
        } catch (IOException e) {
            LogHelper.LogError(e, this.LOGGER);
        }
    }

    /**
     * Partage de la liste des fichiers
     * @param buffIn
     */
    private void shareFilesList(BufferedReader buffIn) {
        try {
            String ip = buffIn.readLine();
            int port = Integer.parseInt(buffIn.readLine());
            int nbFiles = Integer.parseInt(buffIn.readLine());
            ArrayList<String> files = new ArrayList<>();
            for (int i = 0; i < nbFiles; i++) {
                files.add(buffIn.readLine());
            }
            clients.put(new ClientInfo(ip, port, files));
            System.out.println(clientId + " " + ip + " " + port);
        } catch (IOException e) {
            LogHelper.LogError(e, this.LOGGER);
        } catch (InterruptedException e) {
            LogHelper.LogError(e, this.LOGGER);
        }

    }

    /**
     * Fourni la liste des fichiers
     * @param pOut
     */
    private void getFilesList(PrintWriter pOut) {
        ArrayList<FileInfo> files = new ArrayList<>();
        int id=0;
        for (ClientInfo client : clients) {
            for (String file : client.getFiles()) {
                id++;
                files.add(new FileInfo(id, client.getClientAddress(), client.getClientPort(), file));
                this.LOGGER.info(client.getClientAddress() + ":" + client.getClientPort() + " " + file);
            }
        }
        pOut.println(files.size());
        for (FileInfo file : files) {
            pOut.println(file.getIp());
            pOut.println(file.getPort());
            pOut.println(file.getFileName());
        }
    }
}
