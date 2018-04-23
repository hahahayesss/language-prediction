package com.r00t.language;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PredictionProperties {
    private static Properties properties;

    public static void init(String propertiesPath) {
        try {
            properties = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = propertiesPath != null ? new FileInputStream(propertiesPath) : loader.getResourceAsStream("prediction.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFolderLocation() {
        return properties.getProperty("languages.folder");
    }

    public static Integer getLineSize() {
        return Integer.parseInt(properties.getProperty("line.size"));
    }

    public static Integer getMaxCharLimit() {
        return Integer.parseInt(properties.getProperty("max.character.limit"));
    }

    public static String getPossibleCharacters() {
        return properties.getProperty("possible.characters");
    }

    public static Boolean getShuffle() {
        return properties.getProperty("shuffle").equals("true");
    }

    public static Integer getSeed() {
        return Integer.parseInt(properties.getProperty("ml.seed"));
    }

    public static Double getLearningRate() {
        return Double.parseDouble(properties.getProperty("ml.learning.rate"));
    }

    public static Integer getBatchSize() {
        return Integer.parseInt(properties.getProperty("ml.batch.size"));
    }

    public static Integer getNEpochs() {
        return Integer.parseInt(properties.getProperty("ml.n.epochs"));
    }

    public static Integer getHiddenNodex() {
        return Integer.parseInt(properties.getProperty("ml.hidden.nodes"));
    }
}
