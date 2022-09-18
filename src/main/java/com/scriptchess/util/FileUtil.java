package com.scriptchess.util;


import com.scriptchess.exceptions.FileExists;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Description : Utility class for File related operations
 * Author: kumar
 * Created on : 16/09/22
 */

public class FileUtil {

    /**
     * Creates file on given path
     * @param path
     * @throws FileExists
     * @throws IOException
     */
    public static void createFile(String path) throws FileExists, IOException {
        createFile(path, false);
    }

    /**
     * Creates file on given path and overwrite if file exists and overwrite is true
     * @param path
     * @param overwrite
     * @throws FileExists
     * @throws IOException
     */
    public static void createFile(String path, boolean overwrite) throws FileExists, IOException {
        writeData("".getBytes(), overwrite, path);
    }

    /**
     * Writes given data on the file
     * @param data
     * @param overwrite
     * @param path
     * @throws FileExists
     * @throws IOException
     */
    public static void writeData(byte[] data,  boolean overwrite, String path)  throws FileExists, IOException {
        File file = new File(path);
        if(file.exists() && !overwrite)
            throw new FileExists(path + " already exists");
        IOUtils.write(data, new FileOutputStream(file));
    }

    /**
     * Reads whole file and returns in string
     * @param path
     * @return
     * @throws IOException
     */
    public static String readFile(String path) throws IOException {
        File file = new File(path);
        if(!file.exists())
            throw new FileNotFoundException();
        StringBuilder builder = new StringBuilder();
        try (BufferedReader buffer = new BufferedReader(
            new FileReader(path))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                builder.append(str);
            }
        }
        return builder.toString();
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }
}