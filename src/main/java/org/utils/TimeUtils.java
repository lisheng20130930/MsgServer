package org.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static String tranTimeToString(Long tm){
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        return fm.format(new Date(tm));
    }

    public static String dateNow(){
        return tranTimeToString(System.currentTimeMillis());
    }

    public static String dateToStr(Date date){
        try {
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return fm.format(date);
        }catch (Exception e){
            Logger.log(e.getMessage());
        }
        return date.toString();
    }

    public static String getTimeDay(int seek){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date timeNow = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeNow);
        calendar.add(Calendar.DAY_OF_MONTH,seek);
        return df.format(calendar.getTime());
    }

    public static String getTimeQueryYear(){
        Calendar calendar = Calendar.getInstance();
        int date=calendar.get(Calendar.YEAR);
        return date+"-01-01";
    }

    public static String getTimeDay(String day, int seek){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date timeNow = null;
        try {
            timeNow = df.parse(day);
        }catch (Exception e){
            return day;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeNow);
        calendar.add(Calendar.DAY_OF_MONTH,seek);
        return df.format(calendar.getTime());
    }

    public static Long getDiffTime(String endTime, String startTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date one = df.parse(endTime);
            Date two = df.parse(startTime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            return (time1-time2)/1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String getDate(String task_time) {
        return task_time.substring(0,10);
    }

    public static String getDiffTime(Date startTime, Date endTime) {
        try {
            long time1 = endTime.getTime();
            long time2 = startTime.getTime();
            long used = (time1-time2)/1000;
            return (used/60)+"分"+(used%60)+"秒";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAvgTimePretty(String totalTime, String totalCount) {
        try{
            Long total = Long.parseLong(totalTime);
            Long count = Long.parseLong(totalCount);
            if(total > 0 && count > 0){
                long used = total/count;
                return (used/60)+"分"+(used%60)+"秒";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "0秒";
    }

    public static String formatDate(String szDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        try {
            date = df.parse(szDate);
        }catch (Exception e){
            return null;
        }
        return dateToStr(date);
    }
}
