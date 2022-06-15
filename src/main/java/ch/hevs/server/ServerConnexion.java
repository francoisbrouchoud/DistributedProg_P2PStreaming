package ch.hevs.server;

import ch.hevs.common.ClientInfo;
import ch.hevs.common.ServerBase;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.*;

public class ServerConnexion extends ServerBase {
    ArrayBlockingQueue<ClientInfo> clients;
    private static final Logger LOGGER = Logger.getLogger(ServerConnexion.class.getName());

    public ServerConnexion() {
        super();
        clients = new ArrayBlockingQueue<>(20);
        // config logger
        loggerConfig();
    }

    public void loggerConfig(){
        Handler consoleHandler = null;
        Handler fileHandler  = null;
        Formatter simpleFormatter = null;

        //TODO implementer la creation du fichier
        String pattern = "yyyyMM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        try{
            //Creating consoleHandler and fileHandler
            consoleHandler = new ConsoleHandler();
            // chez moi C:\Users\celin\AppData\Local\Temp
            fileHandler  = new FileHandler("%t/serverJavaSocket" + date + ".log", true);
            // Creating SimpleFormatter
            simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);

            //Assigning handlers to LOGGER object
            LOGGER.addHandler(consoleHandler);
            LOGGER.addHandler(fileHandler);

            //Setting levels to handlers and LOGGER
            consoleHandler.setLevel(Level.INFO);
            fileHandler.setLevel(Level.INFO);
            LOGGER.setLevel(Level.INFO);

            LOGGER.config("Configuration done.");

            //Console handler removed
            LOGGER.removeHandler(consoleHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void acceptClient(Socket socket) {
        Thread t = new Thread(new ServerClientConnexion(socket, clients, LOGGER));
        LOGGER.info("New Client connected");
        t.start();
    }
}
