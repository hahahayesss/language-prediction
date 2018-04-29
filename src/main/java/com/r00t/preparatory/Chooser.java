package com.r00t.preparatory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chooser {
    private static Integer choose = 75000;

    public static void main(String[] args) throws IOException {
        List<String> dataList = Files.readAllLines(
                Paths.get("C:\\Users\\hahah\\Desktop\\ML_IN_OUT\\full\\T.csv"),
                Charset.defaultCharset()
        );

        Collections.shuffle(dataList);
        Collections.shuffle(dataList);
        Collections.shuffle(dataList);

        List<String> tempList = new ArrayList<>();
        if (dataList.size() > choose)
            for (int x = 0; x < choose; x++)
                tempList.add(dataList.get(x));

        Files.write(
                Paths.get("C:\\Users\\hahah\\Desktop\\ML_IN_OUT\\full\\T2.csv"),
                tempList,
                Charset.defaultCharset()
        );
    }
}
