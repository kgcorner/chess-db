package com.scriptchess.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 15/11/22
 */

public class JsonUtil {

    public static <T> List<T> getList(String jsonArray, Class<T> clazz) {
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();
        Gson gson = new GsonBuilder().setDateFormat("MMM d, yyyy, hh:mm:ss a").create();
        return gson.fromJson(jsonArray, typeOfT);
    }

    public static String readJson(String fileAddress) throws IOException {
        File file = new File(fileAddress);
        BufferedReader br
            = new BufferedReader(new FileReader(file));

        StringBuffer sb = new StringBuffer();
        String st;
        while ((st = br.readLine()) != null) {
            sb.append(st);
        }
        return sb.toString();
    }

    public static void writeFile(Object data, String path) throws IOException {
        writeFile(new Gson().toJson(data), path);
    }

    public static void writeFile(String data, String path) throws IOException {
        File dataFile = new File(path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile));
        int len = data.length();
        bw.write(data, 0 ,len);
        bw.close();
    }
}