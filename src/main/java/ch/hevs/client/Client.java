package ch.hevs.client;

import ch.hevs.common.ActionClientServer;
import ch.hevs.common.ActionP2P;
import ch.hevs.common.FileInfo;
import ch.hevs.common.SimpleAudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.lang.reflect.Array;
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
public class Client {
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
            System.out.println("**********************************************");
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
            System.out.print("\u2B83 Vous pouvez saisir : (p) pour inscrire  des fichiers à partager | (d) pour demander la liste des fichiers disponibles | (q) pour quitter : ");
            String action = sc.next();

            switch (action) {
                case "p":
                    share(sc);
                    break;
                case "d":
                    ask(sc);
                    break;
                case "q":
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
        System.out.print("\u270E Saisir l'adresse IP du serveur : ");
        do {
            ip = sc.next();
            Matcher matcher = pattern.matcher(ip);
            if (matcher.matches() == true) {
                checkIp = true;
            } else {
                System.err.print("\u2717 L'IP \"" + ip + "\" ne correspond pas au format IPV4. Saisir à nouveau : ");
                checkIp = false;
            }
        } while (!checkIp);

        return InetAddress.getByName(ip);
    }

    private static int portInput(Scanner sc) {
        boolean checkPort;
        int port = -1;
        System.out.print("\u270E Saisir le port du serveur (1024-65535) : ");
        do {
            port = sc.nextInt();
            if (port >= 1024 && port <= 65535) {
                checkPort = true;
            } else {
                System.err.print("\u2717 Le port " + port + " n'est pas valide. Saisir à nouveau dans la plage (1024-65535) : ");
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
        System.out.println("Merci d'avoir utilisé notre application !");
        System.exit(0);
    }

    //TODO gerer les erreurs si le server n'existe pas ConnectException
    private static void share(Scanner console) {
        ArrayList<String> files = new ArrayList<>();
        String file = "";
        do {
            if (!Objects.equals(file, "")) files.add(file);
            System.out.println("\u270E Ecriver le chemin du fichier. Pour arrêter la saisie, entrer  -1 : ");
            file = console.next();
        } while (!Objects.equals(file, "-1"));

        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            pOut.println(ActionClientServer.SHARE_FILE_LIST.ordinal());
            shareFilesList(pOut, files);
            System.out.println("\u2B06 Vos fichiers ont été partagés.");
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

            int idToListen = -1;
            boolean found = false;

            while (!found && files.size() > 0) {
                System.out.print("\u270E Saisir le n° du morceau : ");
                idToListen = console.nextInt();

                for (FileInfo file : files) {
                    if (file.getFileId() == idToListen) {
                        System.out.println("\u266a Morceau choisi : " + file.getFileName());
                        char actionPlay = '-';
                        System.out.print("\u25B6 Jouer (j) | \u2B07 Télécharger (t) | \u293A quitter (q) : ");
                        do {
                            actionPlay = console.next().charAt(0);
                            if (actionPlay == 'j')
                                listen(file);
                            else if (actionPlay == 't')
                                download(file);
                            else if(actionPlay == 'q')
                                System.out.println("\u293A sortie");
                            else
                                System.err.print("Saisir j pour jouer \u25B6 | t pour télécharger \u2B07 | q pour quitter \u293A ");
                        } while (actionPlay != 'j' && actionPlay != 't' && actionPlay !='q');
                        found = true;
                    }
                }

                if (!found)
                    System.err.println("\u274c Morceau non trouvé.");

                System.out.print("\u2B6E Voulez-vous sélectionner un autre morceau (o/n) : ");
                char command = '-';
                do {
                    command = console.next().charAt(0);
                    if (command == 'o')
                        found = false;
                    else if (command == 'n')
                        found = true;
                    else
                        System.err.println("\u274c Saisie non reconnue (o/n) : ");
                } while (command != 'o' && command != 'n');

            }


        } catch (IOException e) {
            e.printStackTrace();
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
                }
                else if (playAction == 'p') {
                    player.pause();
                    System.out.println("\u23F8");
                }
                else if (playAction == 'q') {
                    clientSocket.close();
                    System.out.println("\u23F9 Arrêt de la lecture de " + file.getFileName());
                }
                else{
                    System.err.print("Les commandes valides sont : l = \u25B6 | p = \u23F8 | q = \u23F9 ");
                }

            } while (playAction != 'q');


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
            System.out.println("\u2B07 Téléchargement terminé de " + file.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
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
