package ch.hevs.common;

import ch.hevs.server.ServerConnexion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LogHelper {
    public static void LogError(Exception e, Logger logger){
        logger.severe(e.getMessage());
        for (StackTraceElement trace: e.getStackTrace()) {
            logger.warning(trace.getFileName() +"->" + trace.getClassName()
                    +"->" + trace.getFileName() +"(Line : " + trace.getLineNumber() +")");
        }
    }

    public static Logger loggerConfig(String className){
        Logger logger = Logger.getLogger(className);
        Handler fileHandler  = null;
        Formatter simpleFormatter = null;

        String pattern = "yyyyMM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        try{
            //Creating fileHandler
            // chez moi C:\Users\celin\AppData\Local\Temp
            fileHandler  = new FileHandler("%t/JavaSocket" + className + date + ".log", true);
            // Creating SimpleFormatter
            simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);

            //Assigning handlers to LOGGER object
            logger.addHandler(fileHandler);

            //Setting levels to handlers and LOGGER
            fileHandler.setLevel(Level.INFO);
            logger.setLevel(Level.INFO);

            logger.config("Logger configuration done.");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}
