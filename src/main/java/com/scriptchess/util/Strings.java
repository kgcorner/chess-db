package com.scriptchess.util;


import org.springframework.util.DigestUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 15/09/22
 */

public class Strings {
    private static final Random random = new Random();
    public static boolean isNullOrEmpty(String val) {
        return val == null || val.trim().length() == 0;
    }
    public static String getMd5(String data) {
        return DigestUtils.md5DigestAsHex(data.getBytes());
    }
    public static String createSlug(String val) {
        return val.toLowerCase().replaceAll(" ", "-");
    }

    public static String generateId() {
        Calendar calendar = Calendar.getInstance();
        String YEAR = toHex(calendar.get(Calendar.YEAR));
        String DAY = toHex(calendar.get(Calendar.DAY_OF_MONTH));
        String HOURS = toHex(calendar.get(Calendar.HOUR_OF_DAY));
        String MINUTES = toHex(calendar.get(Calendar.MINUTE));
        String SECOND = toHex(calendar.get(Calendar.SECOND));
        String MILI_SECOND = toHex(calendar.get(Calendar.MILLISECOND));
        if (HOURS.length()  == 1) {
            HOURS = "0" + HOURS;
        }

        if (DAY.length()  == 1) {
            DAY = "0" + DAY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(YEAR).append("-").append(DAY).append("-").append(HOURS).append("-").append(MINUTES).append(SECOND).append(MILI_SECOND);
        return sb.toString();
    }

    public static String getUniqueId() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            //Handle e
        }

        return toHex(System.currentTimeMillis());
    }

    public static String generateUniqueSessionId() {
        return UUID.randomUUID().toString();
    }


    private static int getInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static String createPathFromId(String id) {
        if(id.contains("-")) {
            String[] array = id.split("-");
            String path = "";
            StringBuilder sb = new StringBuilder();
            for (String s : array) {
                sb.append(s);
                sb.append(File.separator);
            }
            path = sb.toString();
            return path;
        } else {
            String[] array = id.split("(?<=\\G.{2})");
            StringBuilder sb = new StringBuilder();
            for (String s : array) {
                sb.append(s);
                sb.append(File.separator);
            }
            return sb.toString();
        }
        //return sb.substring(0, sb.length() -1);
    }

    public static String toHex(long decimal){
        Long rem;
        String hex="";
        char hexchars[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        if(decimal == 0)
            return "00";
        while(decimal>0)
        {
            rem=decimal%16;
            hex=hexchars[rem.intValue()]+hex;
            decimal=decimal/16;
        }
        return hex;
    }

    public static String getString(Byte[] data) {
        byte[] bytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            bytes[i] = data[i];
        }
        return new String(bytes);
    }

    public static byte[] getByteArr(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }



}