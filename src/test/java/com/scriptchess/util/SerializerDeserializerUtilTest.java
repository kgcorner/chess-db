package com.scriptchess.util;

import com.scriptchess.models.Game;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

public class SerializerDeserializerUtilTest {

    @Test
    public void serialize() throws IOException, ClassNotFoundException {

        //Test constructor
        assertNotNull(new SerializerDeserializerUtil());
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        try {
            Game game = new Game();
            game.setSite(fileName);
            SerializerDeserializerUtil.serialize(game, filePath);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            Game result = (Game) ois.readObject();
            assertEquals(game.getSite(), result.getSite());
        } finally {
            File file = new File(filePath);
            file.delete();
        }
    }

    @Test
    public void deSerialize() throws IOException, ClassNotFoundException {
        String fileName  = "weirdName";
        String filePath = DirectoryUtilTest.class.getResource("").getPath() + fileName;
        try {
            Game game = new Game();
            game.setSite(fileName);
            File file = new File(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(game);
            Game result = (Game) SerializerDeserializerUtil.deSerialize(filePath);
            assertEquals(game.getSite(), result.getSite());
        } finally {
            File file = new File(filePath);
            file.delete();
        }
    }
}