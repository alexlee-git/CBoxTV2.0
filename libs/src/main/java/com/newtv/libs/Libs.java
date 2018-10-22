package com.newtv.libs;

import android.annotation.SuppressLint;
import android.content.Context;

import com.newtv.libs.util.NetworkManager;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.libs
 * 创建事件:         11:12
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public class Libs {

    @SuppressLint("StaticFieldLeak")
    private static Libs instance;
    private Context mContext;

    private String mAppKey;
    private String mChannelId;
    private String mFlavor;

    private Libs(Context context, String appkey, String channelId, String flavor) {
        mContext = context.getApplicationContext();
        mAppKey = appkey;
        mChannelId = channelId;
        mFlavor = flavor;

        NetworkManager.init(context);
    }

    public static Libs get() {
        return instance;
    }

    public static void init(Context context, String appkey, String channelId, String flavor) {
        synchronized (Libs.class) {
            if (instance == null) {
                instance = new Libs(context, appkey, channelId, flavor);
            }
        }
    }

    public String getFlavor() {
        return mFlavor;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public Context getContext() {
        return mContext;
    }
}
