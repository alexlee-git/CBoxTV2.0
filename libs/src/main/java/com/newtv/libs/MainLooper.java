package com.newtv.libs;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms
 * 创建事件:         13:28
 * 创建人:           weihaichao
 * 创建日期:          2018/4/12
 */
public class MainLooper {

    private static volatile MainLooper instance;
    private static final int MESSAGE_DELAY = 1;

    private Handler mHandler;

    public static MainLooper get() {
        if (instance == null) {
            synchronized (MainLooper.class) {
                if (instance == null) instance = new MainLooper();
            }
        }
        return instance;
    }

    private MainLooper() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_DELAY:
                        ((Runnable) msg.obj).run();
                        break;
                }
            }
        };
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void postDelayed(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

    public void postSingleDelayed(Runnable runnable, long delay) {
        Message msg = Message.obtain();
        msg.obj = runnable;
        msg.what = MESSAGE_DELAY;
        mHandler.removeMessages(MESSAGE_DELAY);
        mHandler.sendMessageDelayed(msg, delay);
    }

}
