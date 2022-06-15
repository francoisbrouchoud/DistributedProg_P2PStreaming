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

    //Constructor
    public ServerClientConnexion(Socket clientSocketOnServer, ArrayBlockingQueue<ClientInfo> clients, Logger logger) {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientId = clientSocketOnServer.getInetAddress() + ":" + clientSocketOnServer.getPort();
        this.clients = clients;
        this.LOGGER = logger;
    }

    @Override
    public void run() {
        try {
            // création des reader et des writer
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocketOnServer.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocketOnServer.getOutputStream(), true);

            //écoute la commande
            int orderNumber = Integer.parseInt(buffIn.readLine());
            ActionClientServer order = ActionClientServer.values()[orderNumber];

            //TODO switch case des commande possible
            //Création de fonction par cas
            switch (order) {
                case GET_FILES_LIST:
                    this.LOGGER.info("Get files list");
                    getFilesList(pOut);
                    break;
                case SHARE_FILE_LIST:
                    this.LOGGER.info("Share files list");
                    shareFilesList(buffIn);
                    break;
                case DECONNEXION:
                    this.LOGGER.info("Deconnexion");
                    deleteClient(buffIn);
                    break;
            }
            clientSocketOnServer.close();

        } catch (IOException e) {
            LogHelper.LogError(e,this.LOGGER);
        }
    }

    private void deleteClient(BufferedReader buffIn) {
        try {
            String ip = buffIn.readLine();
            int port = Integer.parseInt(buffIn.readLine());

            clients.removeIf(new Predicate<ClientInfo>() {
                @Override
                public boolean test(ClientInfo client) {
                    return client.getClientAdresse().equals(ip) && client.getClientPort() == port;
                }
            });
        } catch (IOException e) {
            LogHelper.LogError(e,this.LOGGER);
        }
    }

    private void shareFilesList(BufferedReader buffIn) {
        try {
            String ip = buffIn.readLine();
            int port = Integer.parseInt(buffIn.readLine());
            int nbFiles = Integer.parseInt(buffIn.readLine());
            ArrayList<String> files = new ArrayList<>();
            for (int i = 0; i < nbFiles; i++) {
                files.add(buffIn.readLine());
            }
            clients.put(new ClientInfo(clientId, ip, port, files));
            System.out.println(clientId + " " + ip + " " + port);
        } catch (IOException e) {
            LogHelper.LogError(e,this.LOGGER);
        } catch (InterruptedException e) {
            LogHelper.LogError(e,this.LOGGER);
        }

    }

    private void getFilesList(PrintWriter pOut) {
        ArrayList<FileInfo> files = new ArrayList<>();
        for (ClientInfo client : clients) {
            for (String file : client.getFiles()) {
                files.add(new FileInfo(client.getClientAdresse(), client.getClientPort(), file));
                this.LOGGER.info(client.getClientAdresse()+":"+client.getClientPort()+" "+ file);
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
