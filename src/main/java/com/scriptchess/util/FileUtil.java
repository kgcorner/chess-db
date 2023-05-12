package com.scriptchess.util;


import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.exceptions.FileExists;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * Description : Utility class for File related operations
 * Author: kumar
 * Created on : 16/09/22
 */

public class FileUtil {
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Logger LOGGER = LogManager.getLogger(FileUtil.class);
    /**
     * Creates file on given path
     * @param path
     * @throws FileExists
     * @throws IOException
     */
    public static void createFile(String path) throws FileExists, IOException {
        createFile(path, false);
    }

    public static List<String> getFilesInDir(String path) throws ChessDbException {
        File dir = new File(path);
        if(!dir.exists()) {
            throw new ChessDbException("Path doesn't exists : " + path);
        }
        if(!dir.isDirectory()) {
            throw new ChessDbException("Path is not directory : " + path);
        }
        List<String> files = new ArrayList<>();
        File[] filesArr = dir.listFiles();
        for(File file : filesArr) {
            files.add(file.getName());
        }
        return files;
    }

    public static List<String> getFiles(String path)  {
        List<String> fileNames = new ArrayList<>();
        File dir = new File(path);
        if(!dir.exists())
            throw new IllegalArgumentException(path+" doesn't exists");
        if(!dir.isDirectory())
            throw new IllegalArgumentException(path+" isn't directory");
        File[] files = dir.listFiles();
        for(File file : files) {
            if(!file.isDirectory())
                fileNames.add(file.getName());
        }
        return fileNames;
    }

    public static List<File> getFilesAndDirs(String path)  {
        List<File> files = new ArrayList<>();
        File dir = new File(path);
        if(!dir.exists())
            throw new IllegalArgumentException(path+" doesn't exists");
        if(!dir.isDirectory())
            throw new IllegalArgumentException(path+" isn't directory");
        File[] filesArr = dir.listFiles();
        for(File file : filesArr) {
            files.add(file);
        }
        return files;
    }

    public static List<String> getFilesRecursively(String dir) {
        List<String> finalFileList = new ArrayList<>();
        List<File> files = FileUtil.getFilesAndDirs(dir);
        for(File file : files) {
            if(file.isDirectory()) {
                List<String> allFiles = getFilesRecursively(file.getAbsolutePath());
                finalFileList.addAll(allFiles);
            } else {
                finalFileList.add(file.getAbsolutePath());
            }
        }
        return finalFileList;
    }

    /**
     * Creates file on given path and overwrite if file exists and overwrite is true
     * @param path
     * @param overwrite
     * @throws FileExists
     * @throws IOException
     */
    public static void createFile(String path, boolean overwrite) throws FileExists, IOException {
        File file = new File(path);
        File parent = new File(file.getParent());
        if(!parent.exists()) {
            parent.mkdirs();
        }
        writeData("".getBytes(), overwrite, path);
    }

    public static void writeData(List<String> lines,  boolean overwrite, String path) {
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            sb.append(line).append("\n");
        }
        writeData(sb.toString().getBytes(), overwrite, path);
    }

    public static void writeByteArrayListData(List<byte[]> list,  boolean overwrite, String path) {
        StringBuilder sb = new StringBuilder();
        FileOutputStream fos = null;
        try {
            if(FileUtil.fileExists(path)) {
                if(!overwrite) {
                    throw new FileExists(path + " already exists");
                }
                FileUtil.copyFile(path, path+".tmp");
                FileUtil.deleteFile(path);
                FileUtil.createFile(path);
            }

            fos = new FileOutputStream(path, true);
            for(byte[] array : list) {
                try {
                    fos.write(array);
                } catch (IOException e) {
                    LOGGER.error(e);
                    FileUtil.deleteFile(path);
                    FileUtil.copyFile( path+".tmp", path);
                }
            }
            FileUtil.deleteFile(path+".tmp");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e);
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }

        //writeData(sb.toString().getBytes(), overwrite, path);
    }

    public static void writeDataWithoutLock(List<String> lines,  boolean overwrite, String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            sb.append(line).append("\n");
        }
        writeDataWithoutLock(sb.toString().getBytes(), overwrite, path);
    }

    /**
     * Writes given data on the file
     * @param data
     * @param overwrite
     * @param path
     * @throws FileExists
     * @throws IOException
     */
    public static void writeData(byte[] data,  boolean overwrite, String path)  throws FileExists {
        Lock lock = LOCK.writeLock();
        lock.lock();
        try {
            writeDataWithoutLock(data, overwrite, path);
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Writes given data on the file. Assumes the call is protected by exclusive lock
     * @param data
     * @param overwrite
     * @param path
     * @throws FileExists
     * @throws IOException
     */
    public static void writeDataWithoutLock(byte[] data,  boolean overwrite, String path) throws FileExists, IOException {
        File file = new File(path);
        if(file.exists() && !overwrite) {
            throw new FileExists(path + " already exists");
        }
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
        Lock lock = LOCK.readLock();
        lock.lock();
        StringBuilder builder = new StringBuilder();
        try (BufferedReader buffer = new BufferedReader(
            new FileReader(path))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                builder.append(str+"\n");
            }
        }
        lock.unlock();
        String data =  builder.toString();
        data = data.trim();
        return data;
    }

    /**
     * Reads whole file and returns in string
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readFileLines(String path) throws FileNotFoundException, IOException {
        File fp = new File(path);
        if(!fp.exists()) {
            return new ArrayList<>();
        }
        FileReader fr = new FileReader(fp);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<String> lines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) { lines.add(line); }

        fr.close();
        return lines;
    }

    /**
     * Reads whole file and returns in string
     * @param stream
     * @return
     * @throws IOException
     */
    public static String readFile(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                builder.append(str+"\n");
            }
        }
        return builder.toString();
    }

    /**
     * Reads whole file and returns in string
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static int readFileSize(String path) throws IOException {
        byte[] bytes =  Files.readAllBytes(Paths.get(path));
        return bytes.length;
    }

    public static boolean deleteFile(String path) {
        File fp = new File(path);
        if(fp.exists()) {
            return fp.delete();
        }
        return true;
    }

    public static void copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destinationFilePath);
        Files.copy(sourceFile.toPath(), destFile.toPath());
    }
}