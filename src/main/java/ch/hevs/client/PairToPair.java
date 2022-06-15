package ch.hevs.client;

import ch.hevs.common.ActionP2P;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PairToPair implements Runnable{
    private Socket clientSocketOnServer;
    private int clientNumber;

    //Constructor
    public PairToPair (Socket clientSocketOnServer, int clientNumber)
    {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientNumber = clientNumber;
    }
    @Override
    public void run() {
        try {
            // création des reader et des writer
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocketOnServer.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocketOnServer.getOutputStream());
            // écoute la commande
            int orderNumber = Integer.parseInt(buffIn.readLine());
            String filePath = buffIn.readLine();
            ActionP2P order = ActionP2P.values()[orderNumber];


            //TODO switch case des commandes possible
            // Création de fonction par cas
            switch (order){
                case LISTEN_AUDIO_FILE:
                    stream(filePath);
                    break;
                case DOWNLOAD_AUDIO_FILE:
                    download(filePath);
                    break;
            }
            clientSocketOnServer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stream(String fileName){
        String filePath = Client.FILES_TO_SHARE_FOLDER +  "\\" + fileName;
        File myFile = new File(filePath);

        long myFileSize = 0;
        try {
            myFileSize = Files.size(Paths.get(filePath));
            //PrintWriter Pout2 = null;
           // Pout2 = new PrintWriter(clientSocketOnServer.getOutputStream(), true);
           // Pout2.println(myFileSize);
           // Pout2.println(fileName);

            byte[] mybytearray = new byte[(int)myFileSize];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            bis.close();

            OutputStream os = null;
            os = clientSocketOnServer.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            clientSocketOnServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private void download(String fileName){
        String filePath = Client.FILES_TO_SHARE_FOLDER +  "\\" + fileName;
        File myFile = new File(filePath);
        long myFileSize = 0;
        try {
            myFileSize = Files.size(Paths.get(filePath));
            PrintWriter Pout2 = null;
            Pout2 = new PrintWriter(clientSocketOnServer.getOutputStream(), true);
            Pout2.println(myFileSize);
            Pout2.println(fileName);

            byte[] mybytearray = new byte[(int)myFileSize];
            BufferedInputStream bis = null;
            bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = null;
            os = clientSocketOnServer.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            clientSocketOnServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
