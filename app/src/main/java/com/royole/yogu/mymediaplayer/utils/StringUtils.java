package com.royole.yogu.mymediaplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * String format
 * Author  yogu
 * Since  2016/6/20
 */


public class StringUtils {
    /**
     * get current time
     * @return
     */
    public static String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
}
