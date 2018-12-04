package com.newtv.libs.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 *  获取当前日期
 */
public class CalendarUtil {
    private static CalendarUtil instance;

    private CalendarUtil() {
        Locale.setDefault(Locale.CHINESE);
    }

    public static CalendarUtil getInstance() {
        if (instance == null) {
            instance = new CalendarUtil();
        }
        return instance;
    }

    private static int translate(int week) {
        int[] weekDays = {1, 2, 3, 4, 5, 6, 7};
        week -= 1;
        if (week < 0)
            week = 0;

        if (week == 0) {
            return 7;
        }
        return weekDays[week];
    }

    public static int getCurrentWeek(){
        return CalendarUtil.getInstance().getWeek();
    }

    /*
     * 判断当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK);
        return translate(w);
    }

    public int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public int getWeek() {
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
        if(week == 0){
            week = 7;
        }
        return week;
    }

    public int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public int getDate() {
        return Calendar.getInstance().get(Calendar.DATE);
    }
}
