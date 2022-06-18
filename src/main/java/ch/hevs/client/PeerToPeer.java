package ch.hevs.client;

import ch.hevs.common.ActionP2P;
import ch.hevs.common.LogHelper;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class PeerToPeer implements Runnable {
    private Socket clientSocketOnServer;
    private int clientNumber;
    private Logger LOGGER;

    public PeerToPeer(Socket clientSocketOnServer, int clientNumber, Logger LOGGER) {
        this.clientSocketOnServer = clientSocketOnServer;
        this.clientNumber = clientNumber;
        this.LOGGER = LOGGER;
    }

    @Override
    public void run() {
        try {
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(clientSocketOnServer.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocketOnServer.getOutputStream());

            //Ecoute de la commande
            int orderNumber = Integer.parseInt(buffIn.readLine());

            ActionP2P order = ActionP2P.values()[orderNumber];

            switch (order) {
                case LISTEN_AUDIO_FILE:
                    this.LOGGER.info("Listen file");
                    stream(buffIn);
                    break;
                case DOWNLOAD_AUDIO_FILE:
                    this.LOGGER.info("Download file");
                    download(buffIn);
                    break;
            }
            clientSocketOnServer.close();

        } catch (IOException e) {
            LogHelper.LogError(e, this.LOGGER);
        }
    }

    /**
     * Stream le fichier audio
     * @param buffIn
     */
    private void stream(BufferedReader buffIn) {
        try {
            String fileName = buffIn.readLine();
            String filePath = Client.FILES_TO_SHARE_FOLDER + "\\" + fileName;
            long myFileSize = Files.size(Paths.get(filePath));
            File myFile = new File(filePath);

            byte[] mybytearray = new byte[(int) myFileSize];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            bis.close();

            OutputStream os = clientSocketOnServer.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            clientSocketOnServer.close();
        } catch (IOException e) {
            LogHelper.LogError(e, this.LOGGER);
        }

    }

    /**
     * Envoi du fichier audio
     * @param buffIn
     */
    private void download(BufferedReader buffIn) {
        try {
            PrintWriter pout = new PrintWriter(clientSocketOnServer.getOutputStream(), true);

            String fileName = buffIn.readLine();
            String filePath = Client.FILES_TO_SHARE_FOLDER + "\\" + fileName;
            File myFile = new File(filePath);
            long myFileSize = Files.size(Paths.get(filePath));

            pout.println(myFileSize);
            pout.println(fileName);

            byte[] mybytearray = new byte[(int) myFileSize];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = null;
            os = clientSocketOnServer.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            clientSocketOnServer.close();
        } catch (IOException e) {
            LogHelper.LogError(e, this.LOGGER);
        }
    }
}
