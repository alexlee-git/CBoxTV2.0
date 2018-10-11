package com.newtv.libs.util;

import android.util.Log;

import com.newtv.libs.MainLooper;
import com.newtv.libs.bean.TimeBean;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by TCP on 2018/5/23.
 */

public class LiveTimingUtil {
    private static final String TAG = "LiveTimingUtil";

    private static final int NET_TIMEOUT = -60 * 1000;
    private static Timer lastTimer;
//    private static SoftReference<LiveEndListener> softLiveEndListener;
    private static LiveEndListener listener;

    public interface LiveEndListener{
        void end();
    }

    /**
     * @param endTime 23:00:11 这种格式
     */
    public static void endTime(final String endTime, final LiveEndListener liveEndListener){
       ServiceTimeUtils.getServiceTime(new ServiceTimeUtils.TimeListener() {

           @Override
           public void success(TimeBean timeBean) {
               Calendar calendar = Calendar.getInstance();
               calendar.setTimeInMillis(timeBean.getResponse());
               dealWith(calendar,endTime,liveEndListener);
           }

           @Override
           public void fail() {
               Calendar calendar = Calendar.getInstance();
               dealWith(calendar,endTime,liveEndListener);
           }
       });
    }

    private static void dealWith(Calendar calendar, String endTime, LiveEndListener liveEndListener){
//        softLiveEndListener = new SoftReference<LiveEndListener>(liveEndListener);
        listener = liveEndListener;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        int currentTime = CmsLiveUtil.formatToSeconds(hour, min, second);
        int end = CmsLiveUtil.formatToSeconds(endTime);
        int delay = (end - currentTime) * 1000;
        if(delay < 0){
            /**
             * 跨天逻辑
             * 跨天一般是20:00--10：00 这样情况  endTime和startTime相差很大
             * 所以如果delay只是-1分钟内,应该不会是跨天，而是由于网络延时导致的
             */
            if(delay < NET_TIMEOUT){
                end += 24 * 60 * 60;
                delay = (end - currentTime) * 1000;
            }

            if(delay < 0){
                delay = 0;
            }
        }

//        delay = 20000;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MainLooper.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if(listener != null){
                            listener.end();
                        }
                        lastTimer = null;
                    }
                });
            }
        },delay);
        Log.i(TAG, "开始计时："+delay);

        cancel();
        lastTimer = timer;
    }

    public static void cancel(){
        if(lastTimer != null){
            lastTimer.cancel();
            lastTimer = null;
        }
    }

    public static void clearListener(){
        listener = null;
    }
}
