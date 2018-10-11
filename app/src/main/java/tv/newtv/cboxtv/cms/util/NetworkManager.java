package tv.newtv.cboxtv.cms.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.newtv.libs.Constant;


/**
 * Created by lixin on 2018/2/11.
 */

public class NetworkManager {

    private static NetworkManager mInstance;

    private Context mContext;
    private ConnectivityManager mConnectManager;

    private NetworkManager() {

    }

    public static NetworkManager getInstance() {
        if (mInstance == null) {
            synchronized (NetworkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetworkManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
