package com.newtv.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by lixin on 2018/2/11.
 */

public class NetworkManager {

    private static NetworkManager mInstance;

    private ConnectivityManager mConnectManager;

    private NetworkManager(Context context) {
        mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkManager getInstance() {
        return mInstance;
    }

    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (NetworkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetworkManager(context);
                }
            }
        }
    }

    public boolean isConnected() {
        if (mConnectManager == null) {
            Log.e(Constant.TAG, "network manager has not been initialized");
            return false;
        }

        NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.e(Constant.TAG, "there is not network");
            return false;
        }
        return true;
    }
}
