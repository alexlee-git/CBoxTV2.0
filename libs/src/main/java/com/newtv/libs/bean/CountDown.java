package com.newtv.libs.bean;

import com.newtv.libs.MainLooper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.bean
 * 创建事件:         17:54
 * 创建人:           weihaichao
 * 创建日期:          2018/4/19
 */
public class CountDown {

    private int mTime;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Listen mListen;

    public CountDown(int seconds) {
        mTime = seconds;
    }

    public void listen(Listen listen) {
        mListen = listen;
    }

    public void start() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mTime--;
                MainLooper.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListen != null) mListen.onCount(mTime);
                    }
                });
                if (mTime == 0) {
                    MainLooper.get().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListen != null) mListen.onComplete();
                        }
                    });
                    CountDown.this.cancel();
                }
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    public void cancel() {
        if (mTime != 0) {
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (mListen != null) mListen.onCancel();
                    mListen = null;
                }
            });
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        mTimerTask = null;
        mTimer = null;
    }

    public interface Listen {
        void onCount(int time);

        void onComplete();

        void onCancel();
    }
}
