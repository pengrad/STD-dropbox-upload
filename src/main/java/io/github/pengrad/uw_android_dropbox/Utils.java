package io.github.pengrad.uw_android_dropbox;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * stas
 * 8/25/15
 */
public class Utils {

    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmss-SSS");

    public static String getFileNameByDate() {
        return FORMATTER.format(new Date());
    }
}
