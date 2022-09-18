package com.scriptchess.util;


import org.springframework.util.DigestUtils;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 15/09/22
 */

public class Strings {
    public static boolean isNullOrEmpty(String val) {
        return val == null || val.trim().length() == 0;
    }
    public static String getMd5(String data) {
        return DigestUtils.md5DigestAsHex(data.getBytes());
    }
}