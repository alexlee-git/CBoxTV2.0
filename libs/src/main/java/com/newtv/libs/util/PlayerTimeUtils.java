package com.newtv.libs.util;

import java.util.Locale;

/**
 * Created by wangkun on 2018/2/7.
 */

public class PlayerTimeUtils {

    private static final String TAG = "TimeUtils";

    private static PlayerTimeUtils mPlayerTimeUtils;

    private PlayerTimeUtils() {
    }

    public static PlayerTimeUtils getInstance(){
        if(mPlayerTimeUtils==null){
            synchronized (PlayerTimeUtils.class){
                if(mPlayerTimeUtils==null){
                    mPlayerTimeUtils = new PlayerTimeUtils();
                }
            }
        }
        return mPlayerTimeUtils;
    }


    // 对节目时长的处理
    public String timeFormat(int time) {
        String returnStr = "00:00:00";
        time = time / 1000;
        long s = time % 60;
        long h = time / 3600L;
        long m = (time / 60) % 60;
        returnStr = String.format(Locale.getDefault(),"%02d", h) + ":" + String.format(Locale.getDefault(),"%02d", m)
                + ":" + String.format(Locale.getDefault(),"%02d", s);
        return returnStr;
    }
}
