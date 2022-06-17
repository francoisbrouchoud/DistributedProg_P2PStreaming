package ch.hevs.client;

import ch.hevs.common.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/***
 * Role:
 * - Demander au serveur une liste de fichiers disponibles
 * - Envoyer la liste des fichiers disponibles depuis le dossier sharedFiles
 * - Récupérer un fichier
 *      - Ecouter le fichier
 *      - Télécharger le fichier
 */
public class Client {
    static final String RECEPTION_FOLDER = "receivedFiles";
    static final String FILES_TO_SHARE_FOLDER = "sharedFiles";
    static ServerPairToPair server;
    public static InetAddress serverAddress;
    public static int serverPort;

    public static void main(String[] args) {

        // création des dossiers de partage et de récéption
        File receptionFolder = new File(RECEPTION_FOLDER);
        if (!receptionFolder.exists()) {
            receptionFolder.mkdir();
        }
        File sharedFolder = new File(FILES_TO_SHARE_FOLDER);
        if (!sharedFolder.exists()) {
            sharedFolder.mkdir();
        }

        // création du server qui partage les fichiers
        server = new ServerPairToPair();
        Thread t = new Thread(server);
        t.start();

        // saisi addresse ip du server
        serverConfig();

        // affichage du menu
        menu();
    }

    private static void serverConfig() {

        System.out.println("**********************************************");
        serverAddress = AddressHelper.ipInput();
        System.out.println("\u2714 Adresse du serveur : " + serverAddress.getHostAddress() + " saisie.");
        serverPort = AddressHelper.portInput();
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
            serverConfig();
        }
    }

    private static void menu() {
        Scanner sc = new Scanner(System.in);

        // Afficher un menu dans la console
        // switch case en fonction des choix
        boolean exit = false;
        do {
            System.out.print("\u2B83 Vous pouvez saisir : (p) pour enregistrer les fichiers à partager | (d) pour demander la liste des fichiers disponibles | (q) pour quitter : ");
            String action = sc.next();
            switch (action) {
                case "p":
                    share(sc);
                    break;
                case "d":
                    ask();
                    break;
                case "q":
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
            pOut.println(ActionClientServer.LOGOUT.ordinal());
            disconnect(pOut);
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
        }
        System.out.println("Merci d'avoir utilisé notre application !");
        System.exit(0);
    }

    private static void share(Scanner console) {
        ArrayList<String> files = new ArrayList<>();

        File localDir = new File(FILES_TO_SHARE_FOLDER);
        File[] localFiles = localDir.listFiles();
        int pos = 0;
        for (File fileElt : localFiles) {
            String fileName = fileElt.getName();
            if (fileElt.isFile() && fileName.substring(Math.max(0, fileName.length() - 4)).equals(".wav")) {
                pos++;
                System.out.println(pos + ") " + fileName);
            }
        }
        if (pos == 0) {
            System.err.println("\u274c Le dossier shareFiles ne contient aucun fichier lisibles. Ajouter des fichiers wav et réessayer. ");
        } else {
            char fileInput;
            do {
                System.out.print("\u270E Ajouter le n° du fichier à partager. Saisir (t) pour terminer la saisie : ");
                fileInput = console.next().charAt(0);
                if (Character.getNumericValue(fileInput) > 0 && Character.getNumericValue(fileInput) <= pos)
                    files.add(localFiles[Character.getNumericValue(fileInput - 1)].getName());
                else if (fileInput == 't')
                    System.out.print("\u2714 Saisie terminée. Contact du serveur en cours... ");
                else
                    System.err.print("\u274c Saisie non reconnue. Réessayer : ");
            } while (fileInput != 't');
        }

        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.SHARE_FILE_LIST.ordinal());
            shareFilesList(pOut, files);
            System.out.println("\u2B06 Vos fichiers ont été partagés.");
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    private static void ask() {
        Scanner console = new Scanner(System.in);
        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.GET_FILES_LIST.ordinal());
            ArrayList<FileInfo> files = getFilesList(buffIn);
            clientSocket.close();
            for (FileInfo file : files) {
                System.out.println(file.getFileId() + ": " + file.getFileName() + " sur " + file.getIp() + ":" + file.getPort());
            }

            int idToListen = -1;
            boolean found = false;

            while (!found && files.size() > 0) {
                System.out.print("\u270E Saisir le n° du morceau : ");
                idToListen = console.nextInt();

                for (FileInfo file : files) {
                    if (file.getFileId() == idToListen) {
                        System.out.println("\u266a Morceau choisi : " + file.getFileName());
                        char actionPlay = '-';
                        System.out.print("\u25B6 Jouer (j) | \u2B07 Télécharger (t) | \u293A Quitter (q) : ");
                        do {
                            actionPlay = console.next().charAt(0);
                            if (actionPlay == 'j')
                                listen(file);
                            else if (actionPlay == 't')
                                download(file);
                            else if (actionPlay == 'q')
                                System.out.println("\u293A sortie");
                            else
                                System.err.print("Saisir (j) pour jouer \u25B6 | (t) pour télécharger \u2B07 | (q) pour quitter \u293A ");
                        } while (actionPlay != 'j' && actionPlay != 't' && actionPlay != 'q');
                        found = true;
                    }
                }

                if (!found) System.err.println("\u274c Morceau non trouvé.");

                System.out.print("\u2B6E Voulez-vous sélectionner un autre morceau (o/n) : ");
                char command = '-';
                do {
                    command = console.next().charAt(0);
                    if (command == 'o')
                        found = false;
                    else if (command == 'n')
                        found = true;
                    else
                        System.err.println("\u274c Saisir (o) pour oui / (n) pour non : ");
                } while (command != 'o' && command != 'n');
            }

        } catch (IOException e) {
            System.err.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    private static void listen(FileInfo file) {
        try {
            Socket clientSocket = new Socket(file.getIp(), file.getPort());

            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionP2P.LISTEN_AUDIO_FILE.ordinal());

            pOut.println(file.getFileName());

            InputStream is = new BufferedInputStream(clientSocket.getInputStream());

            SimpleAudioPlayer player = new SimpleAudioPlayer(is);
            System.out.println("\u266B\u266B\u266B Lecture de " + file.getFileName() + " depuis " + clientSocket.getRemoteSocketAddress() + " \u266B\u266B\u266B");
            System.out.println("Commandes : l = \u25B6 | p = \u23F8 | q = \u23F9 ");

            Scanner sc = new Scanner(System.in);
            char playAction = '-';
            do {
                playAction = sc.next().charAt(0);
                if (playAction == 'l') {
                    player.play();
                    System.out.println("\u25B6");
                } else if (playAction == 'p') {
                    player.pause();
                    System.out.println("\u23F8");
                } else if (playAction == 'q') {
                    clientSocket.close();
                    //TODO voir pour arreter la musique différement
                    player.stop();
                    System.out.println("\u23F9 Arrêt de la lecture de " + file.getFileName());
                } else {
                    System.err.print("Les commandes valides sont : l = \u25B6 | p = \u23F8 | q = \u23F9 ");
                }
            } while (playAction != 'q');
        } catch (UnknownHostException e) {
            System.err.println("Problème d'accès à l'autre client : " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Problème d'accès au fichier : " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Problème de lecture du fichier audio : " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Problème d'accès à l'autre client : " + e.getMessage());
        }
    }

    private static void download(FileInfo file) {
        try {
            Socket clientSocket = new Socket(file.getIp(), file.getPort());

            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionP2P.DOWNLOAD_AUDIO_FILE.ordinal());

            pOut.println(file.getFileName());

            BufferedReader Buffin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

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
            System.out.println("\u2B07 Téléchargement terminé de " + file.getFileName());
        } catch (IOException e) {
            System.err.println("Problème d'accès à l'autre client : " + e.getMessage());
            ask();
        } catch (NumberFormatException e) {
            System.err.println("Problème d'accès au fichier : " + e.getMessage());
            ask();
        }
    }

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
        FileInfo.setUid(0);
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
