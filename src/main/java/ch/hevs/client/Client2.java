package ch.hevs.client;

import ch.hevs.common.ActionClientServer;
import ch.hevs.common.ActionP2P;
import ch.hevs.common.FileInfo;
import ch.hevs.common.SimpleAudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Role:
 * - Demander au server une liste de fichier dispo
 * - Envoyer la liste de fichier dispo sur notre ordi
 * - Récupérer un fichier
 *      - Ecouter le fichier Si music ???
 *      - Download le fichier Si document ???
 */
public class Client2 {
    static final String RECEPTION_FOLDER = "receivedFiles";
    static final String FILES_TO_SHARE_FOLDER = "sharedFiles";
    static ServerPairToPair server;
    public static InetAddress serverAddress;
    public static int serverPort;

    public static void main(String[] args) {
        // Création P2P server --> déplacer dans Serveur
        // activer l'écoute de connexion

        File receptionFolder = new File(RECEPTION_FOLDER);
        if (!receptionFolder.exists()) {
            receptionFolder.mkdir();
        }
        File sharedFolder = new File(FILES_TO_SHARE_FOLDER);
        if (!sharedFolder.exists()) {
            sharedFolder.mkdir();
        }

        server = new ServerPairToPair();
        Thread t = new Thread(server);
        t.start();

        Scanner sc = new Scanner(System.in);

        try {
            serverAddress = ipInput(sc);
            System.out.println("\u2714 Adresse du serveur : " + serverAddress.getHostAddress() + " saisie.");
            serverPort = portInput(sc);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        // Afficher un menu dans la console
        // switch case en fonction des choix
        boolean exit = false;
        do {
            System.out.println("Vous pouvez envoyer des fichiers à partager(p) ou demander la liste des fichier disponnible (d)");
            String action = sc.next();

            switch (action) {
                case "p":
                    share(sc);
                    break;
                case "d":
                    ask(sc);
                    break;
                case "e":
                    exit = true;
                    break;

            }
        } while (!exit);
        exit();
    }

    private static InetAddress ipInput(Scanner sc) throws UnknownHostException {
        String IP_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        Pattern pattern = Pattern.compile(IP_REGEX);
        String ip = "";
        boolean checkIp = false;
        do {
            System.out.print("\u270E Saisir l'adresse IP du serveur : ");
            ip = sc.next();
            Matcher matcher = pattern.matcher(ip);
            if (matcher.matches() == true) {
                checkIp = true;
            } else {
                System.err.println("\u2717 L'IP \"" + ip + "\" ne correspond pas au format IPV4.");
                checkIp = false;
            }
        } while (!checkIp);

        return InetAddress.getByName(ip);
    }

    private static int portInput(Scanner sc) {
        boolean checkPort;
        int port = -1;
        do {
            System.out.print("\u270E Saisir le port du serveur (1024-65535) : ");
            port = sc.nextInt();
            if (port >= 1024 && port <= 65535) {
                checkPort = true;
            } else {
                System.err.println("\u2717 Le port\"" + port+"\" n'est pas valide. ");
                checkPort = false;
            }
        } while (!checkPort);
        return port;
    }

    private static void exit() {
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.DECONNEXION.ordinal());
            disconnect(pOut);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Merci d'avoir utiliser notre application!");
        System.exit(0);
    }

    //TODO gerer les erreurs si le server n'existe pas ConnectException
    private static void share(Scanner console) {
        ArrayList<String> files = new ArrayList<>();
        String file = "";
        do {
            if (!Objects.equals(file, "")) files.add(file);
            System.out.println("ecriver le chemin du fichier si il y en a plus mettre -1: ");
            file = console.next();
        } while (!Objects.equals(file, "-1"));

        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.SHARE_FILE_LIST.ordinal());
            shareFilesList(pOut, files);
            System.out.println("Vos fichiers ont été partagé");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO gerer les erreurs si le server n'existe pas ConnectException
    private static void ask(Scanner console) {
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.GET_FILES_LIST.ordinal());
            ArrayList<FileInfo> files = getFilesList(buffIn);

            for (FileInfo file : files) {
                System.out.println(file.getFileId() + ": " + file.getFileName() + " on " + file.getIp() + ":" + file.getPort());
            }
            clientSocket.close();
            // TODO Ajoute option ecouter un fichier ? faire fonction
            int idToListen = -1;
            boolean found = false;

            while (!found && files.size() > 0) {
                System.out.println("Saisir le n° du morceau : ");
                idToListen = console.nextInt();


                for (FileInfo file : files) {
                    if (file.getFileId() == idToListen) {
                        System.out.println("Morceau choisi : " + file.getFileName());
                        int actionPlay;
                        Scanner sc = new Scanner(System.in);
                        do {
                            System.out.println("Télécharger (1) ou Jouer (2)");
                            actionPlay = sc.nextInt();
                            if (actionPlay == 1) download(file);
                            if (actionPlay == 2) listen(file);
                        } while (actionPlay != 1 && actionPlay != 2);
                        found = true;
                    }
                }
                if (!found) System.out.println("Morceau non trouvé");
                else {
                    System.out.println("Voulez-vous écouter un autre morceau (1/0)");
                    Scanner sc = new Scanner(System.in);
                    int commandMorceau = sc.nextInt();

                    if (commandMorceau == 1) {
                        found = false;
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listen(FileInfo file) {
        System.out.println("Joue " + file.getFileName());
        try{
            Socket clientSocket = new Socket(file.getIp(), file.getPort());
            BufferedReader Buffin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionP2P.GET_AUDIO_FILE.ordinal());

            pOut.println(file.getFileName());
            int totalsize = Integer.parseInt(Buffin.readLine());
            String filename = Buffin.readLine();

            byte[] mybytearray = new byte[totalsize];
            InputStream is = new BufferedInputStream(clientSocket.getInputStream());

            SimpleAudioPlayer player = new SimpleAudioPlayer(is);

            System.out.println("Connexion close" + clientSocket.getRemoteSocketAddress());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private static void download(FileInfo file) {
        System.out.println("Télécharge " + file.getFileName());
        try {
            Socket clientSocket = new Socket(file.getIp(), file.getPort());
            BufferedReader Buffin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionP2P.DOWNLOAD_AUDIO_FILE.ordinal());

            pOut.println(file.getFileName());

            int totalsize = Integer.parseInt(Buffin.readLine());
            String filename = Buffin.readLine();
            byte[] mybytearray = new byte[totalsize];

            InputStream is = new BufferedInputStream(clientSocket.getInputStream());

            FileOutputStream fos = new FileOutputStream(RECEPTION_FOLDER + "\\" + filename);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int byteReadTot = 0;
            while (byteReadTot < totalsize) {
                int byteRead = is.read(mybytearray, 0, mybytearray.length);
                byteReadTot += byteRead;
                bos.write(mybytearray, 0, byteRead);

            }

            bos.close();
            clientSocket.close();
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
        pOut.println(server.getServerAddress().getHostAddress());
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
