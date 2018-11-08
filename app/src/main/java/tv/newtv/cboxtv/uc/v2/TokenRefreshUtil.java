package tv.newtv.cboxtv.uc.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.Utils;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.utils
 * 创建事件:     上午 11:39
 * 创建人:       caolonghe
 * 创建日期:     2018/9/18 0018
 */
public class TokenRefreshUtil {

    private final String TAG = "TokenRefreshUtil";
    private static TokenRefreshUtil mInstance;
    private Disposable mDisposable_Time;

    private TokenRefreshUtil() {
    }

    public static TokenRefreshUtil getInstance() {
        if (mInstance == null) {
            synchronized (TokenRefreshUtil.class) {
                if (mInstance == null) {
                    mInstance = new TokenRefreshUtil();
                }
            }
        }
        return mInstance;
    }

    public boolean isTokenRefresh(Context mContext) {
        boolean isTime;
        String token = SharePreferenceUtils.getToken(mContext);
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        isTime = getTime(mContext);
        Log.i(TAG, "isTime:" + isTime);
        return isTime;
    }

    private boolean isTime;

    private boolean getTime(final Context mContext) {

        long time = TimeUtil.getInstance().getCurrentTimeInMillis();
        long newTime = time / 1000;
        long buildTime = SharePreferenceUtils.getBuildTime(mContext);
        long InvalidTime = SharePreferenceUtils.getInvalidTime(mContext);
        if (buildTime <= newTime && newTime <= (InvalidTime - 2 * 24 * 60 * 60)) {
            Log.i(TAG, "no refresh token");
            isTime = true;
        } else {
            isTime = getRefreshToken(mContext);
            Log.i(TAG, "refresh token");
        }
        return isTime;
    }

    private boolean isToken = false;

    private boolean getRefreshToken(final Context mContext) {

        String refresh_token = SharePreferenceUtils.getrefreshToken(mContext);
        Log.i(TAG, "TokenRefreshService----refresh_token" + refresh_token);
        if (TextUtils.isEmpty(refresh_token)) {
            SharePreferenceUtils.clearToken(mContext);
            return false;
        }
        if (TextUtils.isEmpty(Constant.Authorization)) {
            String Authorization = Utils.getAuthorization(mContext);
            if (!TextUtils.isEmpty(Authorization)) {
                Constant.Authorization = Authorization;
            }
        }
        NetClient.INSTANCE.getUserCenterLoginApi()
                .refreshToken(Constant.Authorization, refresh_token, Constant.CLIENT_ID, Constant.GRANT_TYPE_REFRESH)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable_Time = d;
                    }

                    @Override
                    public void onNext(ResponseBody value) {

                        try {
                            String data = value.string().trim();
                            JSONObject mJsonObject = new JSONObject(data);
                            String mAccessToken = mJsonObject.optString("access_token");
                            String mAccessTokenExpires = mJsonObject.optString("expires_in");
                            String RefreshToken = mJsonObject.optString("refresh_token");

                            SharePreferenceUtils.saveToken(mContext, mAccessToken, RefreshToken);
                            isToken = true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (mDisposable_Time != null) {
                            mDisposable_Time.dispose();
                            mDisposable_Time = null;
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mDisposable_Time != null) {
                            mDisposable_Time.dispose();
                            mDisposable_Time = null;
                        }
                    }
                });
        if (!isToken) {
            SharePreferenceUtils.clearToken(mContext);
        }
        return isToken;
    }
}
