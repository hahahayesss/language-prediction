package com.r00t.language;

import org.apache.commons.lang3.StringUtils;
import org.datavec.api.records.reader.impl.LineRecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LanguageRecordReader extends LineRecordReader {
    private final Integer maxWordLength = PredictionProperties.getMaxCharacterLimit();
    private List<String> wordList;
    private Iterator<String> iterator;

    @Override
    public void initialize(InputSplit split) throws IOException, InterruptedException {
        loadData();

        if (PredictionProperties.getShuffle()) {
            Collections.shuffle(wordList);
            Collections.shuffle(wordList);
            Collections.shuffle(wordList);
        } else
            System.out.println("- WARNING > Shuffle False > For Learning Algorithms shuffled values better");

        iterator = wordList.iterator();

        System.out.println("- INFO > Total records " + wordList.size());
        System.out.println("- INFO > Reader is ready");
    }

    @Override
    public List<Writable> next() {
        if (iterator.hasNext())
            return Arrays.stream(iterator.next().split(","))
                    .map(v -> new DoubleWritable(Double.parseDouble(v)))
                    .collect(Collectors.toList());
        else
            throw new IllegalStateException("- ERROR > There is no item left");
    }

    @Override
    public boolean hasNext() {
        if (iterator != null)
            return iterator.hasNext();
        else
            throw new IllegalStateException("- ERROR > Iterator not ready yet");
    }

    @Override
    public void reset() {
        iterator = wordList.iterator();
    }

    private void loadData() throws IOException {
        wordList = new ArrayList<>();
        for (int x = 0; x < PredictionProperties.getFileLocations().size(); x++) {
            List<String> temp = Files.readAllLines(
                    Paths.get(PredictionProperties.getFileLocations().get(x)),
                    Charset.defaultCharset()
            ).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            Collections.shuffle(temp);
            Collections.shuffle(temp);
            Collections.shuffle(temp);

            if (!PredictionProperties.getLineSize().equals(0L) && temp.size() > PredictionProperties.getLineSize()) {
                List<String> tempList = new ArrayList<>();
                for (int y = 0; y < PredictionProperties.getLineSize(); y++)
                    tempList.add(temp.get(y));
                temp = tempList;
            }

            int finalX = x;
            wordList.addAll(
                    temp.stream()
                            .map(w -> convertWord(w, finalX))
                            .collect(Collectors.toList())
            );
        }
    }

    private String convertWord(String word, Integer language) {
        return getBinaryString(word.toLowerCase()) + "," + language;
    }

    private String getBinaryString(String word) {
        String binaryString = "";
        for (int x = 0; x < word.length(); x++) {
            String fs = StringUtils.leftPad(
                    Integer.toBinaryString(
                            (int) word.charAt(x)
                    ), 5, "0"
            );
            binaryString += fs;
        }
        binaryString = StringUtils.rightPad(binaryString, maxWordLength * 5, "0");
        binaryString = binaryString.replaceAll(".(?!$)", "$0,");
        return binaryString;
    }
}
