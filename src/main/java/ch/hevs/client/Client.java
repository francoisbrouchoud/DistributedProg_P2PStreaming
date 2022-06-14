package ch.hevs.client;

import ch.hevs.common.ActionClientServer;
import ch.hevs.common.FileInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/***
 * Role:
 * - Demander au server une liste de fichier dispo
 * - Envoyer la liste de fichier dispo sur notre ordi
 * - Récupérer un fichier
 *      - Ecouter le fichier Si music ???
 *      - Download le fichier Si document ???
 */
public class Client {
    static ServerPairToPair server;
    public static InetAddress serverAddress;
    public static int serverPort;

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        //TODO scan to find current ip and port

        Scanner sc = new Scanner(System.in);
        //Info pour que le client se connecte sur le serveur
        //TODO handle ip syntax error before exception + evt log
        System.out.print("Saisir l'adresse IP du serveur : ");
        String ip = sc.next();
        System.out.print("Saisir le port du serveur : ");
        int port = sc.nextInt();

        // saisit par l'utilisateur
        try {
            String serverName = ip;//= args[0];
            serverAddress = InetAddress.getByName(serverName);
            System.out.println("Get the address of the server : " + serverAddress);
            serverPort = port;//Integer.parseInt(args[1]);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // Création P2P server --> déplacer dans Serveur
        // activer l'écoute de connexion
        ServerSocket mySkServer = null;
        try {
            mySkServer = new ServerSocket(45000, 10, InetAddress.getByName("localhost"));
            server = new ServerPairToPair(mySkServer);
            Thread t = new Thread(server);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Afficher un menu dans la console
        // switch case en fonction des choix
        boolean exit = false;
        do {
            System.out.println("Vous pouvez envoyer des fichiers à partager(p) ou demander la liste des fichier disponnible (d)");
            String action = console.next();

            switch (action) {
                case "p":
                    share(console);
                    break;
                case "d":
                    ask(console);
                    break;
                case "e":
                    exit = true;
                    break;

            }
        } while (!exit);
        exit();
    }

    private static void exit() {
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.DECONNEXION.ordinal());
            disconnect(pOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Merci d'avoir utiliser notre application!");
        System.exit(0);
    }

    private static void share(Scanner console) {
        ArrayList<String> files = new ArrayList<>();
        String file = "";
        do {
            if (!Objects.equals(file, ""))
                files.add(file);
            System.out.println("ecriver le chemin du fichier si il y en a plus mettre -1: ");
            file = console.next();
        }
        while (!Objects.equals(file, "-1"));

        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.SHARE_FILE_LIST.ordinal());
            shareFilesList(pOut, files);
            System.out.println("Vos fichiers ont été partagé");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ask(Scanner console) {
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.GET_FILES_LIST.ordinal());
            ArrayList<FileInfo> files = getFilesList(buffIn);
            int fileId = 1;
            for (FileInfo file : files) {
                System.out.println(fileId + ": " + file.getFileName() + " on " + file.getIp() + ":" + file.getPort());
                fileId++;
            }
            // TODO Ajoute option ecouter un fichier ? faire fonction
            int idToListen = -1;
            do {
                System.out.println("Ecouter un fichier (1-" + (fileId - 1) + ")");
                idToListen = console.nextInt();

            } while (idToListen !=1 || (idToListen<=0 || idToListen >fileId-1));
            //TO DO play
            /*
            listen(music);
            System.out.println(idToListen);
            */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    private static void listen(int music){
        Socket client = new Socket(IPserverP2P, portServerP2P);
        //Pause + STOp
    }

     */

    private static void disconnect(PrintWriter pOut) {
        pOut.println(server.getServerAddress());
        pOut.println(server.getServerPort());
    }

    private static void shareFilesList(PrintWriter pOut, ArrayList<String> files) {
        pOut.println(server.getServerAddress());
        pOut.println(server.getServerPort());
        pOut.println(files.size());
        for (String file : files) {
            pOut.println(file);
        }
    }

    private static ArrayList<FileInfo> getFilesList(BufferedReader buffIn) {
        try {
            ArrayList<FileInfo> files = new ArrayList<>();
            int size = Integer.parseInt(buffIn.readLine());
            for (int i = 0; i < size; i++) {
                String ip = buffIn.readLine();
                int port = Integer.parseInt(buffIn.readLine());
                String fileName = buffIn.readLine();
                files.add(new FileInfo(ip, port, fileName));
            }
            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
