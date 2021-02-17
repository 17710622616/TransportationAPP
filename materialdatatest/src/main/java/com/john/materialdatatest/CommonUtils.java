package com.john.materialdatatest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

    /**
     * 獲取今日時間
     * @return
     */
    public static String getTimeToday() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(now);
    }

    /**
     * 獲取今日時間
     * @return
     */
    public static String getTimeNow() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(now);
    }

    /**
     * 獲取明天日期
     * @return
     */
    public static String getTomorrowDate() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(tomorrow);
    }

    /**
     * 獲取一个月日期
     * @return
     */
    public static String getLastMonthDate() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(tomorrow);
    }

    /**
     * 获取系统一周前的时间
     * @return
     */
    public static String getlastWeakTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        date = calendar.getTime();
        return df.format(date);
    }
}
