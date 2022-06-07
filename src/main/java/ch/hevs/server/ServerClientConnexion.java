package ch.hevs.server;

import ch.hevs.common.ActionClientServer;
import ch.hevs.common.ClientInfo;
import ch.hevs.common.FileInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;

public class ServerClientConnexion implements Runnable {
    private Socket clientSocketOnServer;
    private String clientId;
    private ArrayBlockingQueue<ClientInfo> clients;

    //Constructor
    public ServerClientConnexion(Socket clientSocketOnServer, ArrayBlockingQueue<ClientInfo> clients) {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientId = clientSocketOnServer.getInetAddress() + ":" + clientSocketOnServer.getPort();
        this.clients = clients;
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
                    getFilesList(pOut);
                    break;
                case SHARE_FILE_LIST:
                    shareFilesList(buffIn);
                    break;
                case DECONNEXION:
                    deleteClient(buffIn);
                    break;
            }
            clientSocketOnServer.close();

        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void getFilesList(PrintWriter pOut) {
        ArrayList<FileInfo> files = new ArrayList<>();
        for (ClientInfo client : clients) {
            for (String file : client.getFiles()) {
                files.add(new FileInfo(client.getClientAdresse(), client.getClientPort(), file));
                //System.out.println(client.getClientAdresse()+":"+client.getClientPort()+" "+ file);
            }
        }
        //System.out.println(files.size());
        pOut.println(files.size());
        for (FileInfo file : files) {
            pOut.println(file.getIp());
            pOut.println(file.getPort());
            pOut.println(file.getFileName());
        }
    }
}
