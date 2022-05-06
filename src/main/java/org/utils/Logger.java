package org.utils;

import com.config.QProfile;

public class Logger {
    static{
        try {
            System.setProperty("log.dir", QProfile.DATA_DIR);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);
    public synchronized static void log(String str){
        logger.warn(str);
    }
}
