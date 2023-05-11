package com.scriptchess.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 10/10/22
 */

public class PGNDateParser {
    public static Date parseDate(String dateStr) {
        Calendar cal = Calendar.getInstance();
        String[] parts = dateStr.split(Pattern.quote("."));
        if(parts[0].length() != 4 || parts[0].contains("?")) {
            return null;
        } else {
            int year = Integer.parseInt(parts[0]);
            cal.set(Calendar.YEAR, year);
        }
        if(!parts[1].contains("?")) {
            int month = Integer.parseInt(parts[1]);
            cal.set(Calendar.MONTH, month - 1);
        }

        if(!parts[2].contains("?")) {
            int day = Integer.parseInt(parts[2]);
            cal.set(Calendar.DAY_OF_MONTH, day);
        }
        return cal.getTime();
    }

    public static String formatDate(Date date) {
        if(date == null)
            return "?";
        return new SimpleDateFormat("yyyy.MM.dd").format(date);
    }

    public static void main(String[] args) throws ParseException {
        String dateStr = "1882.09.25";
        System.out.println(new SimpleDateFormat("YYYY.mm.dd").parse(dateStr));
    }
}