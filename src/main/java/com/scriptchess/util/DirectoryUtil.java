package com.scriptchess.util;


import com.scriptchess.exceptions.DirectoryNotFound;
import com.scriptchess.exceptions.NotADirectoryException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description : Utility class for handling folder related operations
 * Author: kumar
 * Created on : 16/09/22
 */

public class DirectoryUtil {
    /**
     * Creates a directory on given path
     * @param path
     */
    public static boolean createDirectory(String path) {
        File file = new File(path);
        if(file.exists())
            return false;
        else
            file.mkdirs();
        return true;
    }

    /**
     * Returns directories inside given path
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static List<String> getDirectories(String path) throws DirectoryNotFound, NotADirectoryException {
        File file = new File(path);
        if(!file.exists())
            throw new DirectoryNotFound("Could not find" + path );
        if(!file.isDirectory())
            throw new NotADirectoryException(path + " is file not directory");
        File[] files = file.listFiles();
        List<String> directories = new ArrayList<>();
        for(File f : files) {
            if(f.isDirectory())
                directories.add(f.getName());
        }
        return directories;
    }

    /**
     * Returns directories inside given path
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static List<String> getFiles(String path) throws DirectoryNotFound, NotADirectoryException {
        File file = new File(path);
        if(!file.exists())
            throw new DirectoryNotFound("Could not find" + path);
        if(!file.isDirectory())
            throw new NotADirectoryException(path + " is file not directory");
        File[] files = file.listFiles();
        List<String> directories = new ArrayList<>();
        for(File f : files) {
            if(!f.isDirectory())
                directories.add(f.getName());
        }
        return directories;
    }


}