package com.scriptchess.util;


import java.io.*;

/**
 * Description : Utility CLass for Serializing and De-Serializing
 * Author: kumar
 * Created on : 17/09/22
 */

public class SerializerDeserializerUtil {

    /**
     * Serializes an Object
     * @param data
     * @param path
     * @throws IOException
     */
    public static void serialize(Object data, String path) throws IOException {
        File file = new File(path);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(data);
    }

    /**
     * De-Serializes a file
     * @param path
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deSerialize(String path) throws IOException, ClassNotFoundException {
        File file = new File(path);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        return ois.readObject();
    }
}