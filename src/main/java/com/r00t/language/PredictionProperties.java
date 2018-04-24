package com.r00t.language;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PredictionProperties {
    private static Properties properties;

    public static void init(String propertiesPath) throws InterruptedException, IOException {
        properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = propertiesPath != null ? new FileInputStream(propertiesPath) : loader.getResourceAsStream("prediction.xml");
        properties.loadFromXML(inputStream);
        checkRequires();
    }

    private static void checkRequires() throws InterruptedException {
        if (getSeed() == null || getSeed().equals(0))
            throw new InterruptedException("- ERROR > Properties : Seed");
        if (getLearningRate() == null || getLearningRate().equals(0.0))
            throw new InterruptedException("- ERROR > Properties : Learning Rate");
        if (getBatchSize() == null || getBatchSize().equals(0))
            throw new InterruptedException("- ERROR > Properties : Batch Size");
        if (getNEpochs() == null || getNEpochs().equals(0))
            throw new InterruptedException("- ERROR > Properties : nEpochs");
        if (getEvaluation() == null)
            throw new InterruptedException("- ERROR > Properties : Evaluation");
        if (getUIServer() == null)
            throw new InterruptedException("- ERROR > Properties : UI Server");
        if (getOutputFileLocation() == null || getOutputFileLocation().equals("null"))
            throw new InterruptedException("- ERROR > Properties : Output File Location");
        if (getFileLocations().size() == 0)
            throw new InterruptedException("- ERROR > Properties : Language File Locations");
        if (getMaxCharacterLimit() == null || getMaxCharacterLimit().equals(0))
            throw new InterruptedException("- ERROR > Properties : Max Character Limit");
        if (getPossibleCharacters() == null || getPossibleCharacters().equals("null"))
            throw new InterruptedException("- ERROR > Properties : Possible Characters");

    }

    public static Integer getSeed() {
        return Integer.valueOf(properties.getProperty("seed"));
    }

    public static Double getLearningRate() {
        return Double.valueOf(properties.getProperty("learningRate"));
    }

    public static Integer getBatchSize() {
        return Integer.valueOf(properties.getProperty("batchSize"));
    }

    public static Integer getNEpochs() {
        return Integer.valueOf(properties.getProperty("nEpochs"));
    }

    public static Boolean getEvaluation() {
        return Boolean.parseBoolean(properties.getProperty("evaluation"));
    }

    public static Boolean getUIServer() {
        return Boolean.parseBoolean(properties.getProperty("uiServer"));
    }

    public static String getOutputFileLocation() {
        return properties.getProperty("outputFileLocation");
    }

    public static List<String> getFileLocations() {
        List<String> retList = new ArrayList<>();
        int counter = 0;
        for (; ; ) {
            if (properties.getProperty("fileLocation" + counter) == null)
                break;
            retList.add(properties.getProperty("fileLocation" + counter++));
        }
        return retList;
    }

    public static Long getLineSize() {
        return properties.getProperty("lineSize").equals("null") ?
                0L : Long.parseLong(properties.getProperty("lineSize"));
    }

    public static Integer getMaxCharacterLimit() {
        return Integer.parseInt(properties.getProperty("maxCharacterLimit"));
    }

    public static String getPossibleCharacters() {
        return properties.getProperty("possibleCharacters");
    }

    public static Boolean getShuffle() {
        return properties.getProperty("shuffle").equals("null") ?
                true :
                Boolean.parseBoolean(properties.getProperty("shuffle"));
    }

    public static Integer getNumInputs() {
        return properties.getProperty("numInputs").equals("null") ?
                getMaxCharacterLimit() * 5 :
                Integer.parseInt(properties.getProperty("numInputs"));
    }

    public static Integer getNumOutputs() {
        return properties.getProperty("numOutputs").equals("null") ?
                getFileLocations().size() :
                Integer.parseInt(properties.getProperty("numOutputs"));
    }

    public static Integer getNumHiddenNodes() {
        return properties.getProperty("numHiddenNodes").equals("null") ?
                2 * getMaxCharacterLimit() + getNumOutputs() :
                Integer.parseInt(properties.getProperty("numHiddenNodes"));
    }
}
