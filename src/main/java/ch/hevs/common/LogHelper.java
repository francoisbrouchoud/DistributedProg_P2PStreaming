package ch.hevs.common;

import java.util.logging.Logger;

public class LogHelper {
    public static void LogError(Exception e, Logger logger){
        logger.severe(e.getMessage());
        for (StackTraceElement trace: e.getStackTrace()) {
            logger.warning(trace.getFileName() +"->" + trace.getClassName()
                    +"->" + trace.getFileName() +"(Line : " + trace.getLineNumber() +")");
        }
    }
}
