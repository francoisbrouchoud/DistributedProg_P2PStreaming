package ch.hevs.server;

import ch.hevs.client.PairToPair;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

/***
 * Role:
 * - Etre à l'écoute des clients
 * - Stock une liste de fichiers avec ip et port de lieu
 * - Dire l'ip et port pour retrouver un fichier
 * - Lister les fichiers dispo
 */
public class Server {

    Socket srvSocket = null;
    InetAddress localAddress = null;
    PrintWriter pout;
    Scanner scan;

    public static void main(String[] args) {

        //Déplacement serveur BASE
        //TODO scan to find current ip and port
        try {
            //list of all interfaces
            Enumeration<NetworkInterface> allni;
            System.out.println("Liste des connexions disponibles : ");
            ArrayList<InetAddress> inetAddresses = new ArrayList<InetAddress>();
            allni = NetworkInterface.getNetworkInterfaces();
            int connexionNumber = 1;
            while(allni.hasMoreElements()) {
                NetworkInterface nix = allni.nextElement();
                //get the interfaces names if connected
                if (nix.isUp()){
                    Enumeration<InetAddress> LocalAddress =  nix.getInetAddresses();
                    while(LocalAddress.hasMoreElements()) {
                        InetAddress ia = LocalAddress.nextElement();
                        if(!ia.isLinkLocalAddress() && ia instanceof Inet4Address) {
                            if(!ia.isLoopbackAddress()) {
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
            do
            {
                System.out.print("Saisir la connexion voulue (1 - "+ (connexionNumber-1) +") : ");
                Scanner sc = new Scanner(System.in);
                choiceIP = sc.nextInt();

                if(choiceIP < connexionNumber && choiceIP > 0 ){
                    checkChoice = true;
                }
                else{
                    System.err.print("L'interface saisie n'est pas dans la liste. ");
                    System.out.println();
                    checkChoice = false;
                }
            } while (!checkChoice);
            InetAddress localAddress = inetAddresses.get(choiceIP-1);

            int port=-1;
            boolean checkPort;
            do{
                System.out.print("Saisir le port voulu (1024-65535) : ");
                Scanner sc = new Scanner(System.in);
                port = sc.nextInt();
                if(port >= 1024 && port <= 65535){
                    checkPort = true;
                }
                else{
                    System.err.print("Le port saisi n'est pas valide. ");
                    System.out.println();
                    checkPort = false;
                }
            } while (!checkPort);

            System.out.println("Le serveur est atteignable sur l'IP : " + localAddress.getHostAddress() + " sur le port : " + port);

            ServerSocket mySkServer = new ServerSocket(port,10,localAddress);

            ServerConnexion server = new ServerConnexion(mySkServer);
            Thread t = new Thread(server);

            t.start();


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
