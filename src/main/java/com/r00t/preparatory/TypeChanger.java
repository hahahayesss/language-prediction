package com.r00t.preparatory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TypeChanger {
    public static void main(String[] args) throws IOException {
        List<String> dataList = Files.readAllLines(
                Paths.get("C:\\Users\\hahah\\Desktop\\ML_IN_OUT\\full\\D.csv"),
                Charset.forName("ISO-8859-1")
        );

        Files.write(
                Paths.get("C:\\Users\\hahah\\Desktop\\ML_IN_OUT\\full\\D2.csv"),
                dataList,
                Charset.defaultCharset()
        );
    }
}
