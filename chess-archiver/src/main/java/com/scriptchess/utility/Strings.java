package com.scriptchess.utility;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public class Strings {
    public static boolean isNullOrEmpty(String data) {
        return data == null || data.trim().length() == 0;
    }
    public static String getMd5(String data) {
        // Static getInstance method is called with hashing MD5
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 digest found");
        }

        // digest() method is called to calculate message digest
        // of an input digest() return array of byte
        byte[] messageDigest = md.digest(data.getBytes());

        // Convert byte array into signum representation
        BigInteger no = new BigInteger(1, messageDigest);

        // Convert message digest into hex value
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
}