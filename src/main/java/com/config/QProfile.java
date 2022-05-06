package com.config;

import java.io.InputStream;
import java.util.Properties;

public class QProfile {
    public static String DATA_DIR = ".";
    public static String env = null;

    /**
     *  Load Properties
     */
    static{
        try {
            QProfile.load("application.properties");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void load(String configFileName) throws Exception{
        Properties properties = new Properties();
        InputStream in = getResourceAsStream(configFileName);
        properties.load(in);
        in.close();
        loadFromProperties(properties);
    }

    private static InputStream getResourceAsStream(String name) {
        return QProfile.class.getClassLoader().getResourceAsStream(name);
    }

    private static void loadFromProperties(Properties properties){
        env = properties.getProperty("env");
        DATA_DIR = properties.getProperty("data.dir");
    }

    public static String CAT(String p){
        return QProfile.DATA_DIR + "/" + p;
    }
}
