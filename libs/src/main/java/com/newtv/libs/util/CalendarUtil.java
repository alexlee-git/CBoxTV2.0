package com.newtv.libs.util;

import java.util.Calendar;
import java.util.Date;

/*
 *  获取当前日期
 */
public class CalendarUtil {
    private static CalendarUtil instance;


    private CalendarUtil() {

    }

    public static CalendarUtil getInstance() {
        if (instance == null) {
            instance = new CalendarUtil();
        }
        return instance;
    }

    /*
     * 判断当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
//        String [] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int[] weekDays = {0, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        if (w == 0) {
            return 7;
        }
        return weekDays[w];
    }

    public int getHour(){
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute(){
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int getSecond(){
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public int getWeek(){
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public int getYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int getMonth(){
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public int getDate(){
        return Calendar.getInstance().get(Calendar.DATE);
    }
}
