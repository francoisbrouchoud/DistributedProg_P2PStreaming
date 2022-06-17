package ch.hevs.common;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Scanner;

public abstract class ServerBase implements Runnable {
    protected ServerSocket socketServer;
    protected int nbrClient;
    public static InetAddress serverAddress;
    public static int serverPort;

    public ServerBase() {
        this.socketServer = createServer();
    }

    private ServerSocket createServer() {
        try {
            //list of all interfaces
            Enumeration<NetworkInterface> allni;
            System.out.println("\u2315 Liste des connexions disponibles : ");
            ArrayList<InetAddress> inetAddresses = new ArrayList<InetAddress>();
            allni = NetworkInterface.getNetworkInterfaces();
            int connexionNumber = 1;
            while (allni.hasMoreElements()) {
                NetworkInterface nix = allni.nextElement();
                //get the interfaces names if connected
                if (nix.isUp()) {
                    Enumeration<InetAddress> localAddress = nix.getInetAddresses();
                    while (localAddress.hasMoreElements()) {
                        InetAddress ia = localAddress.nextElement();
                        if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                            System.out.println(connexionNumber + "] Interface : " + nix.getName() + " \u2192 IP :  " + ia.getHostAddress());
                            inetAddresses.add(ia);
                            connexionNumber++;
                        }
                    }
                }
            }

            System.out.println("**********************************************");
            Scanner sc = new Scanner(System.in);
            serverAddress = ipAddressAssignment(sc, connexionNumber, inetAddresses);
            serverPort = portAssignment(sc);

            ServerSocket mySkServer = null;
            try {
                mySkServer = new ServerSocket(serverPort, 10, serverAddress);
            } catch (BindException e) {
                System.err.println("\u2717 Le port " + serverPort + " est déjà utilisé. Un port par défaut a été attribué.");
                mySkServer = new ServerSocket(0, 10, serverAddress);
            }

            System.out.println("\u27F7 Le serveur est atteignable à l'adresse IP " + mySkServer.getInetAddress().getHostAddress() + " sur le port " + mySkServer.getLocalPort());
            serverPort = mySkServer.getLocalPort();
            return mySkServer;

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InetAddress ipAddressAssignment(Scanner sc, int connexionNumber, ArrayList<InetAddress> inetAddresses) {
        boolean checkChoice = false;
        int choiceIP = -1;
        System.out.print("\u270E Saisir la connexion voulue (1 - " + (connexionNumber - 1) + ") : ");
        do {
            try{
                choiceIP = sc.nextInt();
                if (choiceIP < connexionNumber && choiceIP > 0) {
                    checkChoice = true;
                } else {
                    System.err.print("\u2717 L'interface saisie n'est pas dans la liste (1 - " + (connexionNumber - 1) + "). Veuillez saisir à nouveau : ");
                    checkChoice = false;
                }
            } catch (InputMismatchException e){
                System.err.println("Saisie d'un nombre attendue :" + e.getMessage());
                checkChoice = false;
            }
        } while (!checkChoice);
        return inetAddresses.get(choiceIP - 1);
    }

    private int portAssignment(Scanner sc) {
        System.out.print("\u2714 IP " + serverAddress.getHostAddress() + " choisie. Sélection automatique du port ? (o/n) : ");
        char choice = '-';
        do {
            choice = sc.next().charAt(0);
            if (choice == 'o') {
                serverPort = 0;
            } else if (choice == 'n') {
                serverPort = AddressHelper.portInput();
            } else {
                System.err.println("\u2717 Veuillez saisir (o/n) : ");
            }
        } while (choice != 'o' && choice != 'n');
        return serverPort;
    }

    public static InetAddress getServerAddress() {
        return serverAddress;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public abstract void acceptClient(Socket socket);

    @Override
    public void run() {
        try {
            //infinite loop
            while (true) {
                Socket socket = socketServer.accept(); // A client wants to connect, we accept him
                acceptClient(socket);

                System.out.println("\u21C4 Client N° " + nbrClient + " connecté");
                nbrClient++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
