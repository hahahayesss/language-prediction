package com.r00t.language;

import org.datavec.api.records.reader.impl.LineRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LanguageRecordReader extends LineRecordReader {
    private Integer maxWordLength;
    private InputSplit inputSplit;
    private List<String> filePaths;
    private List<String> words;
    private Integer totalRecords;
    private Iterator<String> iterator;

    public LanguageRecordReader(Integer maxWordLength) {
        this.maxWordLength = maxWordLength;
        this.filePaths = new ArrayList<>();
        this.words = new ArrayList<>();
    }

    @Override
    public void initialize(InputSplit split) throws IOException, InterruptedException {
        if (split instanceof FileSplit) {
            this.inputSplit = split;

            getFilePaths();
            if (filePaths.size() < 1)
                throw new InterruptedException("- Error > ");

            for (int x = 0; x < filePaths.size(); x++) {
                System.out.println("- INFO > " + filePaths.get(x) + " - " + x);
                words.addAll(convertWords(readFile(filePaths.get(x)), x));
            }
            System.out.println("\n");

            // - Shuffle >>>
            Collections.shuffle(words);
            Collections.shuffle(words);
            Collections.shuffle(words);
            // <<< Shuffle -

            totalRecords = words.size();
            iterator = words.iterator();

            System.out.println("- INFO > Record Size : " + totalRecords);
            System.out.println("\n");
        } else
            throw new InterruptedException("- Error > You have to give FileSplit");
    }

    @Override
    public List<Writable> next() {
        if (iterator.hasNext())
            return Arrays.stream(iterator.next().split(","))
                    .map(v -> new DoubleWritable(Double.parseDouble(v)))
                    .collect(Collectors.toList());
        else
            throw new IllegalStateException("- Error > ");
    }

    @Override
    public boolean hasNext() {
        if (iterator != null)
            return iterator.hasNext();
        else
            throw new IllegalStateException("- Error > ");
    }

    @Override
    public void reset() {
        iterator = words.iterator();
    }

    private void getFilePaths() {
        for (URI uri : inputSplit.locations()) {
            File file = new File(uri);
            if (file.getName().contains(".csv"))
                filePaths.add(file.getAbsolutePath());
        }
        Collections.sort(filePaths);
    }

    private List<String> readFile(String path) throws IOException {
        return Files.readAllLines(
                Paths.get(path),
                Charset.defaultCharset()
        ).stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    private List<String> convertWords(List<String> lines, int language) {
        return lines.stream()
                .map(w -> wordToIntArray(w) + language)
                .collect(Collectors.toList());
    }

    private String wordToIntArray(String word) {
        String retString = "";
        for (int x = 0; x < maxWordLength; x++)
            retString += x < word.length() ? ((int) word.charAt(x) + ",") : "0,";
        return retString;
    }
}
