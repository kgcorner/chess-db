package com.scriptchess.util;

import com.scriptchess.exceptions.FileExists;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

public class FileUtilTest {

    @Test
    public void createFile() throws IOException, FileExists {

        //Test constructor
        assertNotNull(new FileUtil());
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        try {
            FileUtil.createFile(filePath);
            File file = new File(filePath);
            assertTrue(file.exists());
            assertFalse(file.isDirectory());
        } finally {
            File file = new File(filePath);
            file.delete();
        }
    }

    @Test
    public void testCreateFile() throws IOException, FileExists {
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        try {
            FileUtil.createFile(filePath, true);
            File file = new File(filePath);
            assertTrue(file.exists());
            assertFalse(file.isDirectory());
        } finally {
            File file = new File(filePath);
            file.delete();
        }
    }

    @Test(expected = FileExists.class)
    public void testCreateFileWithExistingFile() throws IOException, FileExists {
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        try {
            File file = new File(filePath);
            IOUtils.write("", new FileOutputStream(file));
            FileUtil.createFile(filePath, false);
        } finally {
            File file = new File(filePath);
            file.delete();
        }
    }

    @Test
    public void writeData() throws IOException, FileExists {
        String data = "this needs to be written into the file";
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        FileUtil.writeData(data.getBytes(), true, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        assertFalse(file.isDirectory());
        byte[] resultByte = IOUtils.readFully(new FileInputStream(file), data.length());
        assertEquals(data, new String(resultByte));
    }



    @Test
    public void writeDataIntoExistingFile() throws IOException, FileExists {
        String data = "this needs to be written into the file";
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        File file = new File(filePath);
        IOUtils.write("", new FileOutputStream(file));
        try {
            FileUtil.writeData(data.getBytes(), true, filePath);
            file = new File(filePath);
            assertTrue(file.exists());
            assertFalse(file.isDirectory());
            byte[] resultByte = IOUtils.readFully(new FileInputStream(file), data.length());
            assertEquals(data, new String(resultByte));
        } finally {
            file = new File(filePath);
            file.delete();
        }
    }

    @Test(expected = FileExists.class)
    public void writeDataIntoExistingFileFail() throws IOException, FileExists {
        String data = "this needs to be written into the file";
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        File file = new File(filePath);
        IOUtils.write("", new FileOutputStream(file));
        try {
            FileUtil.writeData(data.getBytes(), false, filePath);
        } finally {
            file = new File(filePath);
            file.delete();
        }
    }

    @Test
    public void readFile() throws IOException {
        String data = "this needs to be written into the file";
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        File file = new File(filePath);
        IOUtils.write(data, new FileOutputStream(file));
        try {
            String result = FileUtil.readFile(filePath);
            assertEquals(data, result);
        } finally {
            file = new File(filePath);
            file.delete();
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void readFileWithNoFile() throws IOException {
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        FileUtil.readFile(filePath);
    }
}