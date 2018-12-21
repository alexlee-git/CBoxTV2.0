package com.newtv.libs;

import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.libs
 * 创建事件:         10:22
 * 创建人:           weihaichao
 * 创建日期:          2018/12/3
 */
public final class ServerTime {

    private static final String TAG = ServerTime.class.getSimpleName();
    private static ServerTime instance;
    private boolean needSyncTime = true;
    private Long serverTime = 0L;
    private Long difference = 0L;

    private static final Long DIFFERENCE_CHECK_INNER = 1000 * 10L;//容错范围  10秒以内的话，忽略时差

    private ServerTime() {
        String timeZone = TimeZone.getDefault().getDisplayName();
        LogUtils.d(TAG, "timezone=" + timeZone);
    }

    public static ServerTime get() {
        if (instance == null) {
            synchronized (ServerTime.class) {
                if (instance == null) instance = new ServerTime();
            }
        }
        return instance;
    }

    public static Long currentTimeMillis() {
        return System.currentTimeMillis() + get().difference;
    }

    public boolean isNeedSyncTime() {
        return needSyncTime;
    }

    public void setServerTime(Long time) {
        if (!needSyncTime) return;
        serverTime = time;
        Long local = System.currentTimeMillis();
        difference = serverTime - local;
        if (Math.abs(difference) < DIFFERENCE_CHECK_INNER) {
            difference = 0L;
        }
        needSyncTime = false;
        LogUtils.d(TAG, "serverTime=" + serverTime);
        LogUtils.d(TAG, "difference=" + difference);
        LogUtils.d(TAG, "needSyncTime=false");
    }

    public void onEnterBackground() {
        needSyncTime = true;
        LogUtils.d(TAG, "onEnterBackground needSyncTime=true");
    }

    public String formatCurrentTime(String s) {
        return format(s, currentTimeMillis());
    }

    public @Nullable
    Date parse(String pattern, String timeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            return simpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String format(String s, Long result) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(s, Locale.CHINA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return simpleDateFormat.format(result);
    }
}
