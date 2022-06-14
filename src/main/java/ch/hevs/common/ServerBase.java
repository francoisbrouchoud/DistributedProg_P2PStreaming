package ch.hevs.common;

import ch.hevs.server.ServerConnexion;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public abstract class ServerBase implements Runnable{
    protected ServerSocket socketServer;
    protected int nbrClient;
    public static InetAddress serverAddress;
    public static int serverPort ;

    public ServerBase(ServerSocket socketServer){
        //TODO create socket server here?
        this.socketServer = socketServer;
        InetAddress localAddress = null;
        PrintWriter pout;
        Scanner scan;
    }

    public static void createServer(){
        try {
            //list of all interfaces
            Enumeration<NetworkInterface> allni;
            System.out.println("Liste des connexions disponibles : ");
            ArrayList<InetAddress> inetAddresses = new ArrayList<InetAddress>();
            allni = NetworkInterface.getNetworkInterfaces();
            int connexionNumber = 1;
            while (allni.hasMoreElements()) {
                NetworkInterface nix = allni.nextElement();
                //get the interfaces names if connected
                if (nix.isUp()) {
                    Enumeration<InetAddress> LocalAddress = nix.getInetAddresses();
                    while (LocalAddress.hasMoreElements()) {
                        InetAddress ia = LocalAddress.nextElement();
                        if (!ia.isLinkLocalAddress() && ia instanceof Inet4Address) {
                            if (!ia.isLoopbackAddress()) {
                                System.out.println("Interface réseau " + connexionNumber + " : " + nix.getName() + " -> IP :  " + ia.getHostAddress());
                                inetAddresses.add(ia);
                                connexionNumber++;
                            }
                        }
                    }
                }
            }

            System.out.println("**********************************************");
            boolean checkChoice = false;
            int choiceIP = -1;
            do {
                System.out.print("Saisir la connexion voulue (1 - " + (connexionNumber - 1) + ") : ");
                Scanner sc = new Scanner(System.in);
                choiceIP = sc.nextInt();

                if (choiceIP < connexionNumber && choiceIP > 0) {
                    checkChoice = true;
                } else {
                    System.err.print("L'interface saisie n'est pas dans la liste. ");
                    System.out.println();
                    checkChoice = false;
                }
            } while (!checkChoice);
            serverAddress = inetAddresses.get(choiceIP - 1);

            int port = -1;
            boolean checkPort;
            do {
                System.out.print("Saisir le port voulu (1024-65535) : ");
                Scanner sc = new Scanner(System.in);
                port = sc.nextInt();
                if (port >= 1024 && port <= 65535) {
                    checkPort = true;
                } else {
                    System.err.print("Le port saisi n'est pas valide. ");
                    System.out.println();
                    checkPort = false;
                }
            } while (!checkPort);

            System.out.println("Le serveur est atteignable sur l'IP : " + serverAddress.getHostAddress() + " sur le port : " + port);

            ServerSocket mySkServer = new ServerSocket(port, 10, serverAddress);

            ServerConnexion server = new ServerConnexion(mySkServer);
            Thread t = new Thread(server);
            t.start();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InetAddress getServerAddress() {
        try {
            return InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return serverAddress;
    }

    public static int getServerPort() {
        return 56000;
        //return serverPort;
    }

    public abstract void acceptClient(Socket socket);
    @Override
    public void run() {
        try {
            //infinite loop
            while(true){
                Socket socket = socketServer.accept(); // A client wants to connect, we accept him
                acceptClient(socket);

                System.out.println("Client Nr "+nbrClient+ " is connected");
                nbrClient++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
