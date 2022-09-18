package com.scriptchess.util;

import com.scriptchess.exceptions.DirectoryNotFound;
import com.scriptchess.exceptions.NotADirectoryException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

public class DirectoryUtilTest {

    @Test
    public void createDirectory() {
        //Test constructor
        assertNotNull(new DirectoryUtil());
        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        boolean created = DirectoryUtil.createDirectory(dirPath);
        assertTrue(created);
        File file = new File(dirPath);
        assertTrue(file.exists());
        assertTrue(file.isDirectory());
        created = DirectoryUtil.createDirectory(dirPath);
        assertFalse(created);
        file.delete();
    }

    @Test
    public void getDirectories() throws IOException {
        String[] dirNames = {"1","2","3","4"};
        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        File file = new File(dirPath);
        file.mkdirs();
        for(String s :dirNames) {
            file = new File(dirPath + File.separator + s);
            file.mkdirs();
        }

        file = new File(dirPath + File.separator + dirName);
        IOUtils.write("", new FileOutputStream(file));

        try {
            List<String> directories = DirectoryUtil.getDirectories(dirPath);
            Collections.sort(directories);
            Object[] result = directories.toArray();
            assertEquals(dirNames, result);
        } catch (DirectoryNotFound directoryNotFound) {
            fail(directoryNotFound.getMessage());
        } catch (NotADirectoryException e) {
            fail(e.getMessage());
        }
        finally {
            for(String s :dirNames) {
                file = new File(dirPath + File.separator + s);
                file.delete();
            }
            file = new File(dirPath + File.separator + dirName);
            file.delete();
            file = new File(dirPath);
            file.delete();

        }
    }

    @Test(expected = NotADirectoryException.class)
    public void getDirectoriesWithFilePath() throws IOException, NotADirectoryException {

        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        File file = new File(dirPath);
        IOUtils.write("", new FileOutputStream(file));


        try {
            DirectoryUtil.getDirectories(dirPath);

        } catch (DirectoryNotFound directoryNotFound) {
            fail(directoryNotFound.getMessage());
        }
        finally {
            file = new File(dirPath);
            file.delete();
        }
    }

    @Test(expected = DirectoryNotFound.class)
    public void getDirectoriesWithNoPath() throws IOException, NotADirectoryException, DirectoryNotFound {

        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        DirectoryUtil.getDirectories(dirPath);
    }

    @Test
    public void getFiles() throws IOException {
        String[] dirNames = {"1","2","3","4"};
        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        File file = new File(dirPath);
        file.mkdirs();
        for(String s :dirNames) {
            file = new File(dirPath + File.separator + s);
            IOUtils.write("", new FileOutputStream(file));
        }
        file = new File(dirPath + File.separator + dirName);
        file.mkdirs();
        try {
            List<String> directories = DirectoryUtil.getFiles(dirPath);
            Collections.sort(directories);
            Object[] result = directories.toArray();
            assertEquals(dirNames, result);
        } catch (DirectoryNotFound directoryNotFound) {
            fail(directoryNotFound.getMessage());
        } catch (NotADirectoryException e) {
            fail(e.getMessage());
        }
        finally {
            for(String s :dirNames) {
                file = new File(dirPath + File.separator + s);
                file.delete();
            }
            file = new File(dirPath);
            file.delete();
            file = new File(dirPath + File.separator + dirName);
            file.delete();
        }
    }

    @Test(expected = NotADirectoryException.class)
    public void getFilesWithFilePath() throws IOException, NotADirectoryException {
        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        File file = new File(dirPath);
        IOUtils.write("", new FileOutputStream(file));


        try {
            DirectoryUtil.getFiles(dirPath);

        } catch (DirectoryNotFound directoryNotFound) {
            fail(directoryNotFound.getMessage());
        }
        finally {
            file = new File(dirPath);
            file.delete();
        }
    }

    @Test(expected = DirectoryNotFound.class)
    public void getFilesWithNoPath() throws IOException, NotADirectoryException, DirectoryNotFound {
        String dirName = "weirdName";
        String dirPath = DirectoryUtilTest.class.getResource("").getPath() + dirName;
        DirectoryUtil.getFiles(dirPath);
    }
}