package ch.hevs.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LogHelper {
    /**
     * Formate un excpetion pour logger son message et sa trace
     * @param e
     * @param logger
     */
    public static void LogError(Exception e, Logger logger) {
        logger.severe(e.getMessage());
        for (StackTraceElement trace : e.getStackTrace()) {
            logger.warning(trace.getFileName() + "->" + trace.getClassName()
                    + "->" + trace.getFileName() + "(Line : " + trace.getLineNumber() + ")");
        }
    }

    /**
     * Création d'une configuration d'un nouveau logger
     * @param className
     * @return logger créé
     */
    public static Logger loggerConfig(String className) {
        Logger logger = Logger.getLogger(className);
        Handler fileHandler = null;
        Formatter simpleFormatter = null;

        String pattern = "yyyyMM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        try {
            // Création Filehandler
            // Dans C:\Users\[user]\AppData\Local\Temp
            fileHandler = new FileHandler("%t/JavaSocket" + className + date + ".log", true);
            // Création SimpleFormatter
            simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);

            // Assigne handlers à l'objet LOGGER
            logger.addHandler(fileHandler);

            // Setting levels to handlers and LOGGER
            fileHandler.setLevel(Level.INFO);
            logger.setLevel(Level.INFO);

            logger.config("Logger configuration done.");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}
