package com.jerry.mouse.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StringUtil {


    public static String getGMTDate(){
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        return sdf.format(cd.getTime());
    }

    public static String getGMTDate(long time){
        return getGMTDate(new Date(time));
    }

    public static String getGMTDate(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        return sdf.format(time);
    }

    public static Date parseGMT(String date){
        if(date == null || date.isEmpty()){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }

    }

    public static String getUTCDate(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(time);
    }



    private static String round(double value){
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        return decimalFormat.format(value);
    }

    public static String formatSize(long fsize){
        if(fsize < 512){
            return (fsize + " B");
        }else if(fsize < 524288){
            return (round(fsize / 1024) + " KB");
        }else if(fsize < 536870912){
            return (round(fsize / 1048576) + " MB");
        }else {
            return (round(fsize / 1073741824) + " GB");
        }
    }


}
