package com.scriptchess.util;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 14/12/22
 */

public class DateUtil {
    public static String getTimeDiff(long end, long start) {
        String diffStr = "";
        long diff = end - start;
        long min = (diff /1000) / 60;
        long sec = (diff /1000);
        if(sec < 1) {
            diffStr = diff+" ms";
        } else {
            if (min < 1) {
                long ms = diff % 1000;
                diffStr = sec + " sec " + ms + " ms";
            } else {
                sec = (diff / 1000) % 60;
                diffStr = min + " min " + sec + " sec";
            }
        }
        return diffStr;
    }

    public static void main(String[] args) {
        long start = 0;
        long end = 500;
        System.out.println(getTimeDiff(start, end));
    }
}