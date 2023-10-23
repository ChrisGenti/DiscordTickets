package com.github.chrisgenti.discordtickets.tools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static String formatDate(Date value) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(value);
    }

    public static Date parseDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Date date;
        try {
            date = format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }
}
