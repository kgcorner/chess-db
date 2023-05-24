package com.scriptchess.util;

import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.exceptions.FileExists;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    public void testGetFileInDir() {
        String[] names = {"a","b","c"};
        File file = new File("/tmp/test");
        if(!file.exists()) {
            file.mkdir();
        }

        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<File> filesAndDirs = FileUtil.getFilesAndDirs(file.getAbsolutePath());
        assertEquals(names.length, filesAndDirs.size());
        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            assertTrue(filesAndDirs.contains(f));
        }
        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            f.delete();
        }
        file.delete();
    }

    @Test
    public void testGetFiles() {
        String[] names = {"a","b","c"};
        File file = new File("/tmp/test1");
        if(!file.exists()) {
            file.mkdir();
        }

        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> filesAndDirs = FileUtil.getFiles(file.getAbsolutePath());
        assertEquals(names.length, filesAndDirs.size());
        for(String a : names) {
            //String path = file.getAbsolutePath() + File.separator + a;
            assertTrue(filesAndDirs.contains(a));
        }
        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            f.delete();
        }
        file.delete();
    }

    @Test
    public void testGetFileAndDir() throws ChessDbException {
        String[] names = {"a","b","c"};
        File file = new File("/tmp/test2");
        if(!file.exists()) {
            file.mkdir();
        }

        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> filesAndDirs = FileUtil.getFilesInDir(file.getAbsolutePath());
        assertEquals(names.length, filesAndDirs.size());
        for(String a : names) {
            //String path = file.getAbsolutePath() + File.separator + a;
            assertTrue(filesAndDirs.contains(a));
        }
        for(String a : names) {
            File f = new File(file.getAbsolutePath() + File.separator + a);
            f.delete();
        }
        file.delete();
    }

    @Test
    public void testGetFilesRecursively() throws ChessDbException {
        String[] names = {"a", "b", "c/c"};
        File file = new File("/tmp/test3");
        if (!file.exists()) {
            file.mkdir();
        }
        try {



            for (String a : names) {
                if (a.contains("/")) {
                    File dir = new File(file.getAbsolutePath() + File.separator + a.split("/")[0]);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                }
                File f = new File(file.getAbsolutePath() + File.separator + a);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<String> filesAndDirs = FileUtil.getFilesRecursively(file.getAbsolutePath());
            assertEquals(names.length, filesAndDirs.size());
            for (String a : names) {
                String path = file.getAbsolutePath() + File.separator + a;
                assertTrue(filesAndDirs.contains(path));
            }
        } finally {
            for(String a : names) {

                File f = new File(file.getAbsolutePath() + File.separator + a);
                f.delete();
                if (a.contains("/")) {
                    File dir = new File(file.getAbsolutePath() + File.separator + a.split("/")[0]);
                    if (dir.exists()) {
                        dir.delete();
                    }
                }
            }
            file.delete();
        }

    }

    @Test
    public void testWriteByteArrayListData() {
        String filePath = "/tmp/fileListTest";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            byte[] totalData = new byte[100];
            List<byte[]> byteArrList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                byte[] tmpData = new byte[10];
                for (int j = 0; j < 10; j++) {
                    tmpData[j] = (byte) j;
                    totalData[(10 * i) + j] = (byte) j;
                }
                byteArrList.add(tmpData);
            }


            FileUtil.writeByteArrayListData(byteArrList, false, filePath);
            try {
                byte[] bytes = FileUtil.readBytes(filePath);
                assertEquals(totalData.length, bytes.length);
                for (int i = 0; i < totalData.length; i++) {
                    assertEquals(totalData[i], bytes[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
        finally {
            file.delete();
        }
    }

    @Test
    public void testWriteByteArrayListDataWithOverwrite() {
        String filePath = "/tmp/fileListTestoverwrite";
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
        try {
            byte[] totalData = new byte[100];
            List<byte[]> byteArrList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                byte[] tmpData = new byte[10];
                for (int j = 0; j < 10; j++) {
                    tmpData[j] = (byte) j;
                    totalData[(10 * i) + j] = (byte) j;
                }
                byteArrList.add(tmpData);
            }

            FileUtil.writeByteArrayListData(byteArrList, true, filePath);
            try {
                byte[] bytes = FileUtil.readBytes(filePath);
                assertEquals(totalData.length, bytes.length);
                for (int i = 0; i < totalData.length; i++) {
                    assertEquals(totalData[i], bytes[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        } finally {
            file.delete();
        }
    }

    @Test
    public void testReadFileLines() {
        String[] lines = {
            "The World Chess Championship begins in Asthana at 9th April 3:00 PM Local Time and 2:30 PM IST",
            "The Official inaugural ceremon was on 7th April.",
            "The whole world will be eyeing for this one event as it's going to decide The New World Chess Champion.",
            "Here we are mentioning the details of the event."
        };
        List<String> stringList = Arrays.asList(lines);
        String filePath = "/tmp/stringList";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileUtil.writeData(stringList, true, filePath);
            try {
                List<String> strings = FileUtil.readFileLines(filePath, false);
                assertEquals(stringList, strings);
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        } finally {
            file.delete();
        }
    }


}